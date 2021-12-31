package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiSendDto;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.push.*;
import com.lguplus.fleta.provider.socket.multi.NettyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Push Multi Client
 * <p>
 * Multi 푸시등록
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PushMultiSocketClientImpl implements PushMultiClient {

    private final NettyClient nettyClient;

    @Value("${push-comm.push.cp.destination_ip}")
    private String destinationIp;

    @Value("${push-comm.push.multi.socket.tps}")
    private String maxLimitPush; //400/1sec

    private static final String DATE_FOMAT = "yyyyMMdd";
    private static final int TRANSACTION_MAX_SEQ_NO = 16*16*16*16;// 10000
    private static final long SECOND = 1000L;
    private static final String SUCCESS = "SC";
    private static final int PROCESS_STATE_REQUEST = 13;
    private static final int COMMAND_REQUEST = 15;
    private static final int FLUSH_COUNT = 20;

    private final AtomicInteger tranactionMsgId = new AtomicInteger(0);
    private final AtomicLong sendMsgCount = new AtomicLong(0);
    private final AtomicLong lastSendMills = new AtomicLong(System.currentTimeMillis());

    private String channelID;
    private final ConcurrentHashMap<String, PushMessageInfoDto> receiveMessageMap = new ConcurrentHashMap<>();
    private final Object sendLock = new Object();

    /**
     * Push Multi 푸시
     *
     * @param dto Push Multi 푸시 정보
     * @return Push Multi 푸시 결과
     */
    @Override
    public PushMultiResponseDto requestPushMulti(PushRequestMultiSendDto dto) {

        // Push GW 서버 connection이 유효한지 확인
        checkGateWayServer();

        synchronized (sendLock)
        {
            // Push 메시지 전송 via G/W (비동기 호출)
            List<PushMultiResponseDto> listUser = sendAsyncMessage(dto);

            // Push 메시지 수신 처리
            ImmutablePair<List<PushMessageInfoDto>, List<PushMultiResponseDto>> requstInfo = parserAsyncMessage(listUser);
            List<PushMessageInfoDto> recvMsgList = requstInfo.getLeft();
            List<PushMultiResponseDto> listFailUser = requstInfo.getRight();

            analyzeReceivedMessage(dto, recvMsgList, listUser, listFailUser);

            log.trace("requestPushMulti recv-count: {}", (long) recvMsgList.size());

            if (listFailUser.isEmpty()) {
                return PushMultiResponseDto.builder().statusCode("200").statusMsg("성공").build();
            } else {
                List<String> failUserList = new ArrayList<>();
                listFailUser.forEach(f -> failUserList.add(f.getRegId()));
                return PushMultiResponseDto.builder().statusCode("1130")
                        .statusMsg("메시지 전송 실패")
                        .failUsers(failUserList)
                        .build();
            }
        }

    }

    @Override
    public void receiveAsyncMessage(PushMessageInfoDto dto) {
        //log.debug("receiveAsyncMessage : {}, {}", dto.getTransactionID(), dto)
        receiveMessageMap.putIfAbsent(dto.getTransactionID(), dto);
    }

    private synchronized void checkGateWayServer() throws NotifyPushRuntimeException {

        if (nettyClient.isInValid() || this.channelID == null) {
            nettyClient.disconnect();
            this.channelID = nettyClient.connect(this);
        }

        // Push GW 서버 connection이 유효한지 확인
        if (nettyClient.isInValid()) {
            throw new SocketException();
        }

        // Channel이 유효한지 확인, 아닌 경우 Channel을 Re-Open함
        if(isServerInValidStatus()) {
            log.debug("[MultiPushRequest][C] the current channel is not valid, re-connect again.");
            nettyClient.disconnect();
            this.channelID = nettyClient.connect(this);	// 재접속 (Channel이 유효하지 않는 경우, 접속 강제 종료 후 재접속함)

            if(isServerInValidStatus()) {
                throw new ServiceUnavailableException();
            }
        }
    }

    private List<PushMultiResponseDto> sendAsyncMessage(PushRequestMultiSendDto dto) {

        List<PushMultiResponseDto> sendList = new ArrayList<>();
        int maxPushLimitPerSec = Integer.parseInt(maxLimitPush);

        long timeMillis = System.currentTimeMillis() - lastSendMills.get();
        if(timeMillis >= SECOND) {
            lastSendMills.set(System.currentTimeMillis());
            sendMsgCount.set(0L);
        }

        for (String regId : dto.getUsers()) {
            String transactionId = getTransactionId();

            final Long sendCount = sendMsgCount.updateAndGet(x ->(x+1 < maxPushLimitPerSec) ? x+1 : 0);

            String jsonMsg = dto.getJsonTemplate().replace(TRANSACT_ID_NM, transactionId)
                    .replace(REGIST_ID_NM, regId);

            nettyClient.write0(PushMessageInfoDto.builder().messageID(COMMAND_REQUEST)
                            .channelID(this.channelID).transactionID(transactionId)
                            .destIp(destinationIp).data(jsonMsg).build());

            sendList.add(PushMultiResponseDto.builder().pushId(transactionId).statusCode("200").regId(regId).build());

            if(sendList.size() % FLUSH_COUNT == 0) {
                nettyClient.flush();
            }

            // TPS 설정에 따른 Time delay : 400건/sec
            if(sendCount == 0) {
                waitTPS();
            }
        }
        nettyClient.flush();

        return sendList;
    }

    private ImmutablePair<List<PushMessageInfoDto>, List<PushMultiResponseDto>>  parserAsyncMessage(List<PushMultiResponseDto> listUser) {

        List<PushMessageInfoDto> recvMsgList = new ArrayList<>();

        int waitTime = 0;
        while(true)
        {
            final Map<String, PushMessageInfoDto> processedMap = recvMsgList.stream().collect(Collectors.toMap(PushMessageInfoDto::getTransactionID, o -> o));
            final List<PushMultiResponseDto> list = listUser.stream().filter(e -> processedMap.get(e.getPushId()) == null).collect(Collectors.toList());

            log.trace("parserAsyncMessage0 [{}] = {}/{}", waitTime, listUser.size() - list.size(), listUser.size());

            for(PushMultiResponseDto usr : list) {
                PushMessageInfoDto responseMsg = getReceivedAsyncMessage(usr.getPushId());
                if(responseMsg != null) {
                    recvMsgList.add(responseMsg);
                }
            }

            try {
                if(list.isEmpty() || waitTime+2 > SECOND * 2) {
                    break;
                }

                waitTime += 2;
                Thread.sleep(2L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        final Map<String, PushMessageInfoDto> processedMap = recvMsgList.stream().collect(Collectors.toMap(PushMessageInfoDto::getTransactionID, o -> o));
        List<PushMultiResponseDto> listFailUser = listUser.stream().filter(e -> processedMap.get(e.getPushId()) == null).collect(Collectors.toList());

        return new ImmutablePair<>(recvMsgList, listFailUser);
    }

    private void analyzeReceivedMessage(PushRequestMultiSendDto dto, List<PushMessageInfoDto> recvMsgList, List<PushMultiResponseDto> listUser, List<PushMultiResponseDto> listFailUser) throws NotifyPushRuntimeException {

        int souccesCnt = 0;

        for(PushMessageInfoDto responseMsg : recvMsgList) {
            String transactionId = responseMsg.getTransactionID();

            // Push 메시지 전송 성공
            if ("200".equals(responseMsg.getStatusCode())) {
                souccesCnt++;
                log.trace("[MultiPushRequest][Push] - {} : {}", souccesCnt, responseMsg);
                continue;
            }

            //exception
            NotifyPushRuntimeException ex = exceptionHandler(responseMsg);
            if(ex != null) {
                throw ex;
            }

            if("410".equals(responseMsg.getStatusCode()) || "412".equals(responseMsg.getStatusCode())) {
                // 유효하지 않은 Reg ID인 경우 오류처리/Retry 없이 그냥 skip
                log.trace("[MultiPushRequest][Push] - skip exception:{} - {}", responseMsg.getStatusCode(), responseMsg);
            } else {
                // 메시지 전송 실패 - Retry 대상
                log.trace("[MultiPushRequest][Push] - FA :serviceId:{} Code:{}", dto.getJsonTemplate(), responseMsg.getStatusCode());

                List<PushMultiResponseDto> list = listUser.stream().filter(p -> p.getPushId().equals(transactionId)).collect(Collectors.toList());
                if(!list.isEmpty())
                    listFailUser.add(list.get(0));
            }
        }

    }

    private NotifyPushRuntimeException exceptionHandler(PushMessageInfoDto responseMsg) {

        if("202".equals(responseMsg.getStatusCode())){
            return new AcceptedException();
        } else if("400".equals(responseMsg.getStatusCode())) {
            return new BadRequestException();
        } else if("401".equals(responseMsg.getStatusCode())) {
            return new UnAuthorizedException();
        } else if("403".equals(responseMsg.getStatusCode())) {
            return new ForbiddenException();
        } else if("404".equals(responseMsg.getStatusCode())) {
            return new NotFoundException();
        }

        return null;
    }

    private PushMessageInfoDto getReceivedAsyncMessage(String transactionId) {

        PushMessageInfoDto responseMsg;

        responseMsg = receiveMessageMap.remove(transactionId);

        if (responseMsg == null) {	// 현재 메모리에 결과가 없는 경우 주어진 시간 동안 대기한 후 다시 읽음
            long readWaited = 0L;
            while (responseMsg == null && readWaited < SECOND * 2) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                responseMsg = receiveMessageMap.remove(transactionId);
                readWaited++;
            }
        }

        return responseMsg;
    }

    private void waitTPS() {

        long timeMillis = System.currentTimeMillis() - lastSendMills.get();
        if (timeMillis < SECOND) {
            try {
                Thread.sleep(SECOND - timeMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.trace("[PushMultiClient][Push] id={} sleep time={}", Thread.currentThread().getId(), SECOND - timeMillis);
        }

        lastSendMills.set(System.currentTimeMillis());
    }

    /**
     * 프로세스 상태 확인 메시지 전송
     */
    private boolean isServerInValidStatus() {
        if (this.channelID == null) {
            return true;
        }

        PushMessageInfoDto response = (PushMessageInfoDto) nettyClient.writeSync(
                PushMessageInfoDto.builder().messageID(PROCESS_STATE_REQUEST)
                        .channelID(this.channelID).destIp(destinationIp)
                        .build());

        if (response != null && SUCCESS.equals(response.getResult())) {
            log.trace("[PushMultiClient] ProcessStateRequest Success. Channel ID : " + channelID);
            return false;
        }

        log.info("[PushMultiClient] ProcessStateRequest Fail. Channel ID : " + channelID);
        return true;
    }

    private String getTransactionId() {
        //return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04d", tranactionMsgId.updateAndGet(x ->(x+1 < TRANSACTION_MAX_SEQ_NO) ? x+1 : 0))
        return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04x", tranactionMsgId.updateAndGet(x ->(x+1 < TRANSACTION_MAX_SEQ_NO) ? x+1 : 0) & 0xFFFF);
    }


}

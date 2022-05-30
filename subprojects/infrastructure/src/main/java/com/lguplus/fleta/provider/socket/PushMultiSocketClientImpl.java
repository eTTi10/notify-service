package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiSendDto;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.push.*;
import com.lguplus.fleta.provider.socket.multi.NettyTcpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
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

    private final NettyTcpClient nettyTcpClient;

    @Value("${push.gateway.default.destination}")
    private String destinationIp;

    @Value("${push.gateway.tps}")
    private int maxLimitPush; //400/1sec

    private static final String DATA_FORMAT = "yyyyMMdd";
    private static final int TRANSACTION_MAX_SEQ_NO = (int)(Math.pow(16, 4));// 65536
    private static final long SECOND = 1000L;
    private static final String SUCCESS = "SC";
    private static final int PROCESS_STATE_REQUEST = 13;
    private static final int COMMAND_REQUEST = 15;
    private static final int FLUSH_COUNT = Integer.sum(100, 0);

    private final AtomicInteger transactionMsgId = new AtomicInteger(0);
    private final AtomicLong sendMsgCount = new AtomicLong(0);
    private final AtomicLong lastSendMills = new AtomicLong(System.currentTimeMillis());

    private String channelID;
    private final ConcurrentHashMap<String, PushMessageInfoDto> receiveMessageMap = new ConcurrentHashMap<>();
    private final Object sendLock = new Object();

    /**
     * Push Multi 푸시
     *
     * @param multiSendDto Push Multi 푸시 정보
     * @return Push Multi 푸시 결과
     */
    @Override
    public PushMultiResponseDto requestPushMulti(PushRequestMultiSendDto multiSendDto) {

        // Push GW 서버 connection이 유효한지 확인
        checkGateWayServer();

        synchronized (sendLock)
        {
            // Push 메시지 전송 via G/W (비동기 호출)
            final List<PushMultiResponseDto> sendUsers = sendAsyncMessage(multiSendDto);
            log.debug("requestPushMulti thread:{} reguser-count: {}",  Thread.currentThread().getId(), sendUsers.size());

            // Push 메시지 수신 처리
            Pair<List<PushMessageInfoDto>, List<PushMultiResponseDto>> requstInfo = parserAsyncMessage(sendUsers);
            List<PushMessageInfoDto> receivedMessages = requstInfo.getLeft();
            List<PushMultiResponseDto> lastNotReceivedMessages = requstInfo.getRight();

            analyzeReceivedMessage(multiSendDto, receivedMessages, sendUsers, lastNotReceivedMessages);

            log.debug("requestPushMulti thread:{} recv-count: {}",  Thread.currentThread().getId(), receivedMessages.size());

            if (lastNotReceivedMessages.isEmpty()) {
                return PushMultiResponseDto.builder().statusCode("200").statusMsg("성공").build();
            } else {
                List<String> failUserList = new ArrayList<>();
                lastNotReceivedMessages.forEach(f -> failUserList.add(f.getRegId()));
                return PushMultiResponseDto.builder().statusCode("1130")
                        .statusMsg("메시지 전송 실패")
                        .failUsers(failUserList)
                        .build();
            }
        }

    }

    @Override
    public void receiveAsyncMessage(MsgType msgType, PushMessageInfoDto dto) {
        receiveMessageMap.put(dto.getTransactionId(), dto);
    }

    public synchronized void checkGateWayServer() throws NotifyRuntimeException {

        checkClientInvalid();

        checkClientProcess();

        checkInvalidServerException();
    }

    public void checkClientInvalid() {
        if (nettyTcpClient.isInValid() || this.channelID == null) {
            nettyTcpClient.disconnect();
            this.channelID = nettyTcpClient.connect(this);
        }

        // Push GW 서버 connection이 유효한지 확인
        if (nettyTcpClient.isInValid()) {
            throw new SocketException();
        }
    }

    public void checkClientProcess() {
        // Channel이 유효한지 확인, 아닌 경우 Channel을 Re-Open함
        if(isServerInValidStatus()) {
            log.debug("[MultiPushRequest][C] the current channel is not valid, re-connect again.");
            nettyTcpClient.disconnect();
            this.channelID = nettyTcpClient.connect(this);	// 재접속 (Channel이 유효하지 않는 경우, 접속 강제 종료 후 재접속함)
        }
    }

    public void checkInvalidServerException() {
        if(isServerInValidStatus()) {
            throw new ServiceUnavailableException();
        }
    }

    private List<PushMultiResponseDto> sendAsyncMessage(PushRequestMultiSendDto multiSendDto) {

        List<PushMultiResponseDto> sendUsers = new ArrayList<>();
        int maxPushLimitPerSec = maxLimitPush;

        long timeMillis = System.currentTimeMillis() - lastSendMills.get();
        if(timeMillis >= SECOND) {
            lastSendMills.set(System.currentTimeMillis());
            sendMsgCount.set(0L);
        }

        for (String regId : multiSendDto.getUsers()) {
            String transactionId = getTransactionId();

            final long sendCount = sendMsgCount.updateAndGet(x ->(x+1 < maxPushLimitPerSec) ? x+1 : 0);

            String jsonMsg = multiSendDto.getJsonTemplate().replace(TRANSACT_ID_NM, transactionId)
                    .replace(REGIST_ID_NM, regId);

            nettyTcpClient.write(PushMessageInfoDto.builder().messageId(COMMAND_REQUEST)
                            .channelId(this.channelID).transactionId(transactionId)
                            .destinationIp(destinationIp).data(jsonMsg).build());

            sendUsers.add(PushMultiResponseDto.builder().pushId(transactionId).statusCode("200").regId(regId).build());

            if(sendUsers.size() % FLUSH_COUNT == 0) {
                nettyTcpClient.flush();
            }

            // TPS 설정에 따른 Time delay : 400건/sec
            if(sendCount == 0) {
                waitTPS();
            }
        }
        nettyTcpClient.flush();

        return sendUsers;
    }

    private Pair<List<PushMessageInfoDto>, List<PushMultiResponseDto>>  parserAsyncMessage(List<PushMultiResponseDto> sendUsers) {

        List<PushMessageInfoDto> receivedMessages = new ArrayList<>();

        int waitTime = 0;
        while(true)
        {
            final Map<String, PushMessageInfoDto> processedMap = receivedMessages.stream().collect(Collectors.toMap(PushMessageInfoDto::getTransactionId, o -> o));
            final List<PushMultiResponseDto> notReceivedMessages = sendUsers.stream().filter(e -> processedMap.get(e.getPushId()) == null)
                    .collect(Collectors.toList());

            log.trace("parserAsyncMessage0 thread:{} [{}] = {}/{}", Thread.currentThread().getId(), waitTime, sendUsers.size() - notReceivedMessages.size(), sendUsers.size());

            for (PushMultiResponseDto usr : notReceivedMessages) {
                PushMessageInfoDto responseMsg = receiveMessageMap.remove(usr.getPushId());
                if (responseMsg != null) {
                    log.debug("parserAsyncMessage1 msg: {}", responseMsg);
                    receivedMessages.add(responseMsg);

                    //exception check
                    if(!"200".equals(responseMsg.getStatusCode())) {
                        exceptionHandler(responseMsg);
                    }
                }
            }

            if(notReceivedMessages.isEmpty() || waitTime+2 > SECOND * 2) {
                break;
            }

            waitTime += 2;
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(2L));
        }

        final Map<String, PushMessageInfoDto> processedMap = receivedMessages.stream().collect(Collectors.toMap(PushMessageInfoDto::getTransactionId, o -> o));
        List<PushMultiResponseDto> lastNotReceivedMessages = sendUsers.stream().filter(e -> processedMap.get(e.getPushId()) == null).collect(Collectors.toList());

        log.debug("parserAsyncMessage thread:{} time:{} = {}/{}", Thread.currentThread().getId(), waitTime, sendUsers.size() - lastNotReceivedMessages.size(), sendUsers.size());
        return Pair.of(receivedMessages, lastNotReceivedMessages);
    }

    private void analyzeReceivedMessage(PushRequestMultiSendDto multiSendDto, List<PushMessageInfoDto> receivedMessages, List<PushMultiResponseDto> sendUsers, List<PushMultiResponseDto> lastNotReceivedMessages) throws NotifyRuntimeException {

        int souccesCnt = 0;

        for(PushMessageInfoDto messageInfoDto : receivedMessages) {
            String transactionId = messageInfoDto.getTransactionId();

            // Push 메시지 전송 성공
            if ("200".equals(messageInfoDto.getStatusCode())) {
                souccesCnt++;
                log.trace("[MultiPushRequest][Push] - {} : {}", souccesCnt, messageInfoDto);
            }
            else {
                if ("410".equals(messageInfoDto.getStatusCode()) || "412".equals(messageInfoDto.getStatusCode())) {
                    // 유효하지 않은 Reg ID인 경우 오류처리/Retry 없이 그냥 skip
                    log.debug("[MultiPushRequest][Push] - skip exception:{} - {}", messageInfoDto.getStatusCode(), messageInfoDto);
                } else {
                    // 메시지 전송 실패 - Retry 대상
                    log.debug("[MultiPushRequest][Push] - FA :serviceId:{} Code:{}", multiSendDto.getJsonTemplate(), messageInfoDto.getStatusCode());

                    List<PushMultiResponseDto> list = sendUsers.stream().filter(p -> p.getPushId().equals(transactionId)).collect(Collectors.toList());
                    lastNotReceivedMessages.add(list.get(0));
                }
            }
        }

    }

    private void exceptionHandler(PushMessageInfoDto messageInfoDto) {

        if("202".equals(messageInfoDto.getStatusCode())){
            throw new AcceptedException();
        } else if("400".equals(messageInfoDto.getStatusCode())) {
            throw new BadRequestException();
        } else if("401".equals(messageInfoDto.getStatusCode())) {
            throw new UnAuthorizedException();
        } else if("403".equals(messageInfoDto.getStatusCode())) {
            throw new ForbiddenException();
        } else if("404".equals(messageInfoDto.getStatusCode())) {
            throw new NotFoundException();
        }
    }

    public void waitTPS() {

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
        Optional<PushMessageInfoDto> response = nettyTcpClient.writeSync(PushMessageInfoDto.builder()
                        .messageId(PROCESS_STATE_REQUEST)
                        .channelId(this.channelID).destinationIp(destinationIp)
                        .build());
        return !response.orElse(PushMessageInfoDto.builder().result("FA").build()).getResult().equals(SUCCESS);
    }

    private String getTransactionId() {
        return DateFormatUtils.format(new Date(), DATA_FORMAT) + String.format("%04x", transactionMsgId.updateAndGet(x ->(x+1 < TRANSACTION_MAX_SEQ_NO) ? x+1 : 0) & 0xFFFF);
    }


}

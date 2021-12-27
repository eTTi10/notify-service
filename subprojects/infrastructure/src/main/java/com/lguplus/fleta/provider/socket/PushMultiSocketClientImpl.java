package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiSendDto;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.push.*;
import com.lguplus.fleta.provider.socket.multi.MsgEntityCommon;
import com.lguplus.fleta.provider.socket.multi.NettyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Push Multi Client
 * <p>
 * Multi 푸시등록
 */
@Slf4j
//@ToString
@RequiredArgsConstructor
@Component
//@EnableScheduling
public class PushMultiSocketClientImpl implements PushMultiClient {

    private final NettyClient nettyClient;

    @Value("${push-comm.push.server.ip}")
    private String host;

    @Value("${push-comm.push.server.port}")
    private String port;

    @Value("${server.port}")
    private String channelPort;

    @Value("${push-comm.push.socket.channelID}")
    private String defaultChannelHost;

    @Value("${push-comm.push.cp.destination_ip}")
    private String destinationIp;

    @Value("${push-comm.push.multi.socket.tps}")
    private String maxLimitPush; //400/1sec
    private int maxPushLimitPerSec;

    private static final String DATE_FOMAT = "yyyyMMdd";
    private static final int TRANSACTION_MAX_SEQ_NO = 9999;
    private static final String REGIST_ID_NM = "[@RegistId]";
    private static final String TRANSACT_ID_NM = "[@RegistId]";
    private static final long SECOND = 1000;

    private final AtomicInteger commChannelNum = new AtomicInteger(0);
    private final AtomicInteger tranactionMsgId = new AtomicInteger(0);

    private String channelID;
    private final ConcurrentHashMap<String, PushMessageInfoDto> receiveMessageMap = new ConcurrentHashMap<>();


    @PostConstruct
    private void initialize() {

        maxPushLimitPerSec = Integer.parseInt(maxLimitPush);

        nettyClient.initailize(this, host, Integer.parseInt(port));
        channelConnectionRequest();

    }

    /**
     * Push Multi 푸시
     *
     * @param dto Push Multi 푸시 정보
     * @return Push Multi 푸시 결과
     */
    @Override
    public PushMultiResponseDto requestPushMulti(PushRequestMultiSendDto dto) {
        /*
        if (!nettyClient.isValid()) {
            nettyClient.initailize(this, host, Integer.parseInt(port));
            channelConnectionRequest();
        }
        */

        //checkServerStatus();

      //  return PushMultiResponseDto.builder().statusCode("200").build();


        // Push GW 서버 connection이 유효한지 확인
        checkGateWayServer();

        // Push 메시지 전송 via G/W (비동기 호출)
        List<PushMultiResponseDto> sendList = sendAsyncMessage(dto);

        List<PushMultiResponseDto> listUser = sendList.stream().filter(r -> r.getStatusCode().equals("200")).collect(Collectors.toList());
        List<PushMultiResponseDto> listFailUser = sendList.stream().filter(r -> r.getStatusCode().equals("")).collect(Collectors.toList());

        // Push 메시지 수신 처리
        List<PushMessageInfoDto> recvMsgList = receiveAsyncMessage(listUser, listFailUser);

        analyzeReceivedMessage(recvMsgList, listUser, listFailUser);

        if(listFailUser.isEmpty()) {
            return PushMultiResponseDto.builder().statusCode("200").statusMsg("성공").build();
        }
        else {
            List<String> failUserList = new ArrayList<>();
            listFailUser.forEach(f -> failUserList.add(f.getRegId()));
            return PushMultiResponseDto.builder().statusCode("1130")
                    .statusMsg("메시지 전송 실패")
                    .failUsers(failUserList)
                    .build();
        }

    }

    @Override
    public void receiveAsyncMessage(PushMessageInfoDto dto) {
        receiveMessageMap.putIfAbsent(dto.getTransactionID(), dto);
    }

    private void checkGateWayServer() throws NotifyPushRuntimeException {
        // Push GW 서버 connection이 유효한지 확인
        if (!nettyClient.isValid()) {
            nettyClient.connect();
            if(!nettyClient.isValid()) {
                //exceptionHandler("pushgw.socket")
                throw new SocketException();
            }
            // 접속 요청 메시지 전송
            channelConnectionRequest();
        }

        // Channel이 유효한지 확인, 아닌 경우 Channel을 Re-Open함
        if(checkServerStatus()) {
            log.debug("[MultiPushRequest][C] the current channel is not valid, re-connect again.");
            nettyClient.disconnect();
            nettyClient.connect();	// 재접속 (Channel이 유효하지 않는 경우, 접속 강제 종료 후 재접속함)

            if(channelConnectionRequest() && checkServerStatus()) {
                throw new ServiceUnavailableException();
            }
        }
    }

    private List<PushMultiResponseDto> sendAsyncMessage(PushRequestMultiSendDto dto) {
        int sendCount = 0;
        long sendStartTimeMills = System.currentTimeMillis();
        List<PushMultiResponseDto> sendList = new ArrayList<>();

        //test
        List<String> testUser = new ArrayList<>();

        for(int i=0; i<100; i++) {
            testUser.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        }

        dto.setUsers(testUser);

        for (String regId : dto.getUsers()) {
            String transactionId = getTransactionId();
            String jsonMsg = dto.getJsonTemplate().replace(TRANSACT_ID_NM, transactionId).replace(REGIST_ID_NM, regId);

            String sendStatus = commandRequest(transactionId, jsonMsg);

            sendList.add(PushMultiResponseDto.builder().pushId(transactionId).statusCode(sendStatus).regId(regId).build());

            // TPS 설정에 따른 Time delay : 400건/sec
            if((++sendCount) % maxPushLimitPerSec == 0) {
                sendStartTimeMills = waitTPS(sendCount, sendStartTimeMills);
            }
        }

        return sendList;
    }

    private List<PushMessageInfoDto> receiveAsyncMessage(List<PushMultiResponseDto> listUser, List<PushMultiResponseDto> listFailUser) {

        List<PushMessageInfoDto> recvMsgList = new ArrayList<>();

        for(PushMultiResponseDto usr : listUser) {
            String transactionId =  usr.getPushId();

            PushMessageInfoDto responseMsg = getReceivedAsyncMessage(transactionId);
            if ( responseMsg == null || !MsgEntityCommon.SUCCESS.equals(responseMsg.getResult())) {
                listFailUser.add(usr);
                continue;
            }

            recvMsgList.add(responseMsg);
        }

        return recvMsgList;
    }

    private void analyzeReceivedMessage(List<PushMessageInfoDto> recvMsgList, List<PushMultiResponseDto> listUser, List<PushMultiResponseDto> listFailUser) throws NotifyPushRuntimeException {

        int souccesCnt = 0;

        for(PushMessageInfoDto responseMsg : recvMsgList) {
            String transactionId = responseMsg.getTransactionID();

            // Push 메시지 전송 성공
            if ("200".equals(responseMsg.getStatusCode())) {
                souccesCnt++;
                log.debug("[MultiPushRequest][Push] - {} : {}", souccesCnt, responseMsg);
                continue;
            }

            //exception
            NotifyPushRuntimeException ex = exceptionHandler(responseMsg);
            if(ex != null) {
                throw ex;
            }

            if("410".equals(responseMsg.getStatusCode()) || "412".equals(responseMsg.getStatusCode())) {
                // 유효하지 않은 Reg ID인 경우 오류처리/Retry 없이 그냥 skip함
                log.debug("[MultiPushRequest][Push] - skip exception:{} - {}", responseMsg.getStatusCode(), responseMsg);
            } else {
                // 메시지 전송 실패 - Retry 대상
                log.debug("[MultiPushRequest][Push] - skip exception:{} - {}", responseMsg.getStatusCode(), responseMsg);

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

    private Long waitTPS(int sendCount, Long startTimeMills) {
        long currentTimeMills = System.currentTimeMillis();
        long timeMillis = currentTimeMills - startTimeMills;
        if (timeMillis < SECOND) {
            try {
                Thread.sleep(SECOND - timeMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.debug("[MultiPushRequest][Push] sleep for " + sendCount + ", id=" + Thread.currentThread().getId());
        }
        return System.currentTimeMillis();
    }

    /**
     * Channel 접속 요청 메시지 전송
     */
    private boolean channelConnectionRequest() {
        String genChannelID = this.getNextChannelID();

        PushMessageInfoDto message = PushMessageInfoDto.builder()
                .messageID(MsgEntityCommon.CHANNEL_CONNECTION_REQUEST)
                .channelID(genChannelID)
                .destIp(destinationIp)
                .build();

        if (nettyClient.write(message)) {
            this.channelID = genChannelID;
            log.debug("[MessageService] ChannelConnectionRequest Success. Channel ID : " + channelID);
            return true;
        }

        log.error("[MessageService] ChannelConnectionRequest Fail.");
        return false;
    }

    /**
     * 프로세스 상태 확인 메시지 전송
     */
    private boolean checkServerStatus() {
        if (this.channelID == null) {
            return true;
        }

        PushMessageInfoDto message = PushMessageInfoDto.builder()
                .messageID(MsgEntityCommon.PROCESS_STATE_REQUEST)
                .channelID(this.channelID)
                .destIp(destinationIp)
                .build();

        PushMessageInfoDto response = (PushMessageInfoDto) nettyClient.writeSync(message);

        if (response != null && MsgEntityCommon.SUCCESS.equals(response.getResult())) {
            log.debug("[MessageService] ProcessStateRequest Success. Channel ID : " + channelID);
            return false;
        }

        log.info("[MessageService] ProcessStateRequest Fail. Channel ID : " + channelID);
        return true;
    }

    /**
     * 명령어 처리 요청 메시지 전송
     */
    private String commandRequest(String transactionId, String jsonMsg) {

        PushMessageInfoDto message = PushMessageInfoDto.builder()
                .messageID(MsgEntityCommon.COMMAND_REQUEST)
                .channelID(this.channelID)
                .transactionID(transactionId)
                .destIp(destinationIp)
                .data(jsonMsg)
                .build();

        return nettyClient.write(message) ? "200" : "";
    }

    private String getNextChannelID() {
        if(commChannelNum.get() >= 9999) {
            commChannelNum.set(0);
        }

        String hostname;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostname = defaultChannelHost;
        }

        hostname = hostname.replace("DESKTOP-", "");
        hostname = hostname + hostname;

        String channelHostNm = (hostname + "00000000").substring(0, 6);
        String channelPortNm = (channelPort + "0000").substring(0, 4);

        return channelHostNm + channelPortNm + String.format("%04d", commChannelNum.incrementAndGet());
    }

    private String getTransactionId() {
        if(tranactionMsgId.get() >= TRANSACTION_MAX_SEQ_NO) {
            tranactionMsgId.set(0);
            return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04d", tranactionMsgId.get());
        }
        return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04d", tranactionMsgId.incrementAndGet());
    }

}

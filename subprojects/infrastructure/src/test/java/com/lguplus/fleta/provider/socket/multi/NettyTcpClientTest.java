package com.lguplus.fleta.provider.socket.multi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiSendDto;
import com.lguplus.fleta.data.dto.response.inner.PushMessageInfoDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class NettyTcpClientTest implements PushMultiClient {

    NettyTcpClient nettyTcpClient = new NettyTcpClient();
    String channelID;

    List<PushRequestItemDto> addItems = new ArrayList<>();
    PushRequestMultiDto pushRequestMultiDto;
    List<String> users = new ArrayList<>();

    AtomicInteger tranactionMsgId = new AtomicInteger(0);
    static int testCnt = 9997;

    @BeforeEach
    void setUp() {
        //nettyTcpClient = new NettyTcpClient();

        ReflectionTestUtils.setField(nettyTcpClient, "host", "211.115.75.227");
        ReflectionTestUtils.setField(nettyTcpClient, "port", "9600");
        ReflectionTestUtils.setField(nettyTcpClient, "timeout", "2000");
        ReflectionTestUtils.setField(nettyTcpClient, "channelPort", "8080");
        ReflectionTestUtils.setField(nettyTcpClient, "defaultChannelHost", "PsAGT");
        ReflectionTestUtils.setField(nettyTcpClient, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(nettyTcpClient, "pushCallRetryCnt", "2");

        ReflectionTestUtils.setField(nettyTcpClient, "commChannelNum", new AtomicInteger(++testCnt));

        this.channelID = nettyTcpClient.connect(this);
        //boolean check = nettyTcpClient.isInValid();

        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");

        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        pushRequestMultiDto = PushRequestMultiDto.builder()
                .serviceId("30011")
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .users(users)
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();
    }

    private String getMessage(PushRequestMultiDto dto) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", PushMultiClient.PUSH_COMMAND);
        paramMap.put("push_id", PushMultiClient.TRANSACT_ID_NM);
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getApplicationId());
        paramMap.put("noti_contents", dto.getMessage());
        paramMap.put("service_passwd", getSha512Pwd(dto.getServiceId()));

        if (PushMultiClient.LG_PUSH_OLD.equals("00007")) {
            paramMap.put("push_app_id", "smartux0001");
            paramMap.put("noti_type", "POS");
            paramMap.put("regist_id", PushMultiClient.REGIST_ID_NM);
        } else {
            paramMap.put("service_key", PushMultiClient.REGIST_ID_NM);
        }

        dto.getItems().forEach(e -> paramMap.put(e.getItemKey(), e.getItemValue()));

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode oNode = objectMapper.createObjectNode();
        oNode.set("request", objectMapper.valueToTree(paramMap));
        return oNode.toString();
    }

    private String getSha512Pwd(String servicePwd) {
        // service_pwd : SHA512 암호화
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(servicePwd.getBytes(StandardCharsets.UTF_8));
            return String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("기타 오류");
        }
    }

    @Override
    public PushMultiResponseDto requestPushMulti(PushRequestMultiSendDto dto) {
        return null;
    }

    PushMessageInfoDto receivedMessage = null;
    MsgType receivedMsgType;
    CountDownLatch receivedLatch;// = new CountDownLatch(1);
    @Override
    public void receiveAsyncMessage(MsgType msgType, PushMessageInfoDto dto) {
        if(msgType == MsgType.SEND_SUCCESS_MSG || msgType == MsgType.SEND_FAIL_MSG) {
            receivedMessage = dto;
            receivedMsgType = msgType;
            receivedLatch.countDown();
        }
        else if(msgType == MsgType.RECIVED_MSG) {
            log.debug("** receiveAsyncMessage {} , {}", msgType, dto);
        }
    }

    private String getTransactionId() {
        String DATE_FOMAT = "yyyyMMdd";
        return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04x", tranactionMsgId.updateAndGet(x ->(x+1 < 10000) ? x+1 : 0) & 0xFFFF);
    }

    @Test
    void test_requestPushMulti() throws InterruptedException {
        String transactionId = getTransactionId();
        int COMMAND_REQUEST = 15;

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();
        String jsonMsg = dto.getJsonTemplate().replace(TRANSACT_ID_NM, transactionId)
                .replace(REGIST_ID_NM, pushRequestMultiDto.getUsers().get(0));

        receivedLatch = new CountDownLatch(1);

        PushMessageInfoDto sendDto = PushMessageInfoDto.builder().messageId(COMMAND_REQUEST)
                .channelId(this.channelID).transactionId(transactionId)
                .destinationIp("222.231.13.85").data(jsonMsg).build();

        log.debug("nettyTcpClient.isInValid {} {} {}", nettyTcpClient.isInValid(), this.channelID, sendDto);
        Assertions.assertFalse(nettyTcpClient.isInValid());
        nettyTcpClient.write(sendDto);
        nettyTcpClient.flush();

        receivedLatch.await(2000, TimeUnit.MILLISECONDS);

        Assertions.assertEquals(MsgType.SEND_SUCCESS_MSG, receivedMsgType);
        Assertions.assertEquals(transactionId, receivedMessage.getTransactionId());

        Thread.sleep(2000);
        nettyTcpClient.disconnect();
    }

    @Test
    void test_requestPushMulti2() throws InterruptedException {
        String transactionId = getTransactionId();
        int PROCESS_STATE_REQUEST = 13;

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();
        String jsonMsg = dto.getJsonTemplate().replace(TRANSACT_ID_NM, transactionId)
                .replace(REGIST_ID_NM, pushRequestMultiDto.getUsers().get(0));

        PushMessageInfoDto response = (PushMessageInfoDto) nettyTcpClient.writeSync(
                PushMessageInfoDto.builder().messageId(PROCESS_STATE_REQUEST)
                        .channelId(this.channelID).destinationIp("222.231.13.85").build());

        int processSatusId = response.getMessageId();
        Assertions.assertEquals(14, processSatusId);

        Thread.sleep(2000);
        nettyTcpClient.disconnect();

    }

    @AfterEach
    void tearDown() {


    }

}
package com.lguplus.fleta.provider.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiSendDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;
import com.lguplus.fleta.exception.push.*;
import com.lguplus.fleta.provider.socket.multi.NettyTcpClient;
import com.lguplus.fleta.provider.socket.multi.NettyTcpJunitServerTest;
import fleta.util.JunitTestUtils;
import io.netty.bootstrap.Bootstrap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class})
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
class PushMultiSocketClientImplTest {

    static NettyTcpJunitServerTest server;
    static Thread thread;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 9600;

    static int testCnt = 9997;

    List<PushRequestItemDto> addItems = new ArrayList<>();
    PushRequestMultiDto pushRequestMultiDto;
    List<String> users = new ArrayList<>();
    PushMultiSocketClientImpl pushMultiSocketClient;
    NettyTcpClient nettyTcpClient;

    @BeforeAll
    static void setUpAll() {
        server = new NettyTcpJunitServerTest();
        thread = new Thread(() -> {
            server.runServer(SERVER_PORT);
        });
        thread.start();
    }

    @AfterAll
    static void setUpClose() {
        server.stopServer();
    }

    @BeforeEach
    void setUp() {
        nettyTcpClient = getNettyClient();
        pushMultiSocketClient = new PushMultiSocketClientImpl(nettyTcpClient);
        ReflectionTestUtils.setField(pushMultiSocketClient, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(pushMultiSocketClient, "maxLimitPush", "5");
        ReflectionTestUtils.setField(pushMultiSocketClient, "FLUSH_COUNT", 2);
        ReflectionTestUtils.setField(pushMultiSocketClient, "transactionMsgId", new AtomicInteger((int) (Math.pow(16, 4)) - 3));

        for (int i = 0; i < 10; i++) {
            users.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        }

        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        pushRequestMultiDto = PushRequestMultiDto.builder().serviceId("30011").pushType("G").applicationId("lguplushdtvgcm").users(users).message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"").items(addItems).build();

        server.responseCode = "200";
        server.responseCount = 0;
        server.responseTestMode = "normal";
    }

    private NettyTcpClient getNettyClient() {
        NettyTcpClient nettyTcpClient = new NettyTcpClient();

        ReflectionTestUtils.setField(nettyTcpClient, "host", SERVER_IP);
        ReflectionTestUtils.setField(nettyTcpClient, "port", "" + SERVER_PORT);
        ReflectionTestUtils.setField(nettyTcpClient, "timeout", "2000");
        ReflectionTestUtils.setField(nettyTcpClient, "wasPort", "8080");
        ReflectionTestUtils.setField(nettyTcpClient, "defaultSocketChannelId", "PsAGT");
        ReflectionTestUtils.setField(nettyTcpClient, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(nettyTcpClient, "callRetryCount", "2");

        ReflectionTestUtils.setField(nettyTcpClient, "commChannelNum", new AtomicInteger(++testCnt));

        return nettyTcpClient;
    }

    private String getMessage(PushRequestMultiDto dto) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", PushMultiClient.PUSH_COMMAND);
        paramMap.put("push_id", PushMultiClient.TRANSACT_ID_NM);
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getApplicationId());
        paramMap.put("noti_contents", dto.getMessage());
        paramMap.put("service_passwd", dto.getServiceId());

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

    @Test
        //normal case
    void testServer01() {

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();
        PushMultiResponseDto responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("200", responseMultiDto.getStatusCode());

    }

    @Test
        //timeMillis >= SECOND
    void testServer02() {

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();

        ReflectionTestUtils.setField(pushMultiSocketClient, "lastSendMills", new AtomicLong(System.currentTimeMillis() - 5000));
        PushMultiResponseDto responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("200", responseMultiDto.getStatusCode());

    }

    @Test
        //exception check
    void testServer03() {

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers().stream().limit(1).collect(toList())).build();

        int[] errCode = new int[]{202, 400, 401, 403, 404};
        Class[] exlist = new Class[]{AcceptedException.class, BadRequestException.class, UnAuthorizedException.class, ForbiddenException.class, NotFoundException.class};

        for (int i = 0; i < errCode.length; i++) {
            server.responseCode = "" + errCode[i];

            assertThrows(exlist[i], () -> {
                pushMultiSocketClient.requestPushMulti(dto);
            });
        }

    }

    @Test
        //exception check : 410, 412, other
    void testServer04() {

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers().stream().limit(2).collect(toList())).build();

        server.responseCode = "410";
        PushMultiResponseDto responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("200", responseMultiDto.getStatusCode());

        server.responseCode = "412";
        responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("200", responseMultiDto.getStatusCode());

        server.responseCode = "499";
        responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        Assertions.assertEquals("1130", responseMultiDto.getStatusCode());

        //NettyTcpServerTest.responseTestMode
    }

    @Test
        // test mode abnormal
    void testServer05() {

        server.responseTestMode = "abnormal";

        //Make Message
        PushRequestMultiSendDto dto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(pushRequestMultiDto)).users(pushRequestMultiDto.getUsers()).build();

        PushMultiResponseDto responseMultiDto = pushMultiSocketClient.requestPushMulti(dto);
        log.debug("testServer05: {}", responseMultiDto);
        Assertions.assertEquals("1130", responseMultiDto.getStatusCode());
    }

    @Test
        // isServerInValidStatus
    void testServer06_isServerInValidStatus() throws Exception {

        //Connect
        Method methodCheck = pushMultiSocketClient.getClass().getDeclaredMethod("checkGateWayServer");
        methodCheck.setAccessible(true);
        methodCheck.invoke(pushMultiSocketClient);

        //Server Status
        Method method = pushMultiSocketClient.getClass().getDeclaredMethod("isServerInValidStatus");

        server.responseProcessFlag = "1";
        method.setAccessible(true);
        boolean status = (boolean) method.invoke(pushMultiSocketClient);
        Assertions.assertFalse(status);

        server.responseProcessFlag = "0";
        status = (boolean) method.invoke(pushMultiSocketClient);
        Assertions.assertTrue(status);

        server.responseProcessFlag = ""; // not return
        status = (boolean) method.invoke(pushMultiSocketClient);
        Assertions.assertTrue(status);

        //throw ServiceUnavailableException
        server.responseProcessFlag = "0";
        assertThrows(ServiceUnavailableException.class, () -> {
            pushMultiSocketClient.checkInvalidServerException();
        });

        server.responseProcessFlag = "1";
        pushMultiSocketClient.checkInvalidServerException();

    }

    @Test
    void testServer07_checkGateWayServer() throws Exception {

        ReflectionTestUtils.setField(nettyTcpClient, "port", "1" + SERVER_PORT); //unknown port
        assertThrows(SocketException.class, () -> {
            pushMultiSocketClient.checkClientInvalid();
        });

        JunitTestUtils.setValue(pushMultiSocketClient, "channelID", "01234567890ABCD"); //test channel Id
        assertThrows(SocketException.class, () -> {
            pushMultiSocketClient.checkClientInvalid();
        });

        //normal
        ReflectionTestUtils.setField(nettyTcpClient, "port", "" + SERVER_PORT);
        Bootstrap bootstrap = (Bootstrap)ReflectionTestUtils.getField(nettyTcpClient, "bootstrap");
        bootstrap.remoteAddress(SERVER_IP, SERVER_PORT);
        nettyTcpClient.disconnect();
        String channelId = nettyTcpClient.connect(pushMultiSocketClient); //normal connect
        pushMultiSocketClient.checkClientInvalid();

        //abnormal\
        server.responseProcessFlag = "0"; //process check error
        pushMultiSocketClient.checkClientProcess();
    }

    @Test
    void testServer09_waitTPS() throws Exception {
        //connect
        long lastTime = System.currentTimeMillis() - 3000;
        JunitTestUtils.setValue(pushMultiSocketClient, "lastSendMills", new AtomicLong(lastTime)); //test channel Id
        pushMultiSocketClient.waitTPS();
        AtomicLong time2 = (AtomicLong) ReflectionTestUtils.getField(pushMultiSocketClient, "lastSendMills");
        assertTrue(time2.get() > lastTime);

        final class TestThread extends Thread {
            @Override
            public void run() {
                while (true) {
                    //setTime = ;
                    JunitTestUtils.setValue(pushMultiSocketClient, "lastSendMills", new AtomicLong(System.currentTimeMillis() - 500)); //test channel Id
                    pushMultiSocketClient.waitTPS();
                }
            }
        }

        TestThread thread = new TestThread();//.run();
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();

        AtomicLong time3 = (AtomicLong) ReflectionTestUtils.getField(pushMultiSocketClient, "lastSendMills");
        assertTrue(time2.get() < time3.get());

    }

}
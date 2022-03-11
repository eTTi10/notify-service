package com.lguplus.fleta.provider.socket.smsagent;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.provider.socket.multi.NettyTcpJunitServerTest;
import fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

@Slf4j
@ExtendWith({MockitoExtension.class})
@TestMethodOrder(MethodOrderer.MethodName.class)
class SmsGatewayTest {

    static NettyTcpJunitServerTest server;
    static Thread thread;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 8999;

    String id = "@id";
    String password = "@password";

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
    }

    SmsGateway getSmsGateWay() {
        return new SmsGateway(SERVER_IP, SERVER_PORT + "", id, password);
    }

    SmsGateway getInvaildSmsGateWay() {
        return new SmsGateway(SERVER_IP, "1" + SERVER_PORT, id, password);
    }

    @Test
    void test_01_() throws IOException {
        SmsGateway gateway = getSmsGateWay();

        int port = gateway.getPort();
        assertEquals(SERVER_PORT, port);

        SmsGateway spy_gw = spy(gateway);
        assertTrue(spy_gw.isBind());

        spy_gw.sendMessage("01041112222", "01041113333", "callback", "test", 1);

        //reconnect
        JunitTestUtils.setValue(gateway, "RECONNECT_TERM", 3000);
        ReflectionTestUtils.invokeMethod(spy_gw, "connectGateway");
        assertTrue(spy_gw.isBind());

        //checkLink
        JunitTestUtils.setValue(gateway, "LINK_CHECK_TERM", 1000);
        ReflectionTestUtils.invokeMethod(spy_gw, "checkLink");

        //sendReport
        ReflectionTestUtils.invokeMethod(spy_gw, "sendReport");
    }

    //Invalid Socket Port
    @Test
    void test_02_() {
        SmsGateway gateway = getInvaildSmsGateWay();
        assertFalse(gateway.isBind());
    }

    //readBufferToString
    @Test
    void test_03_() {
        int testValue = 10;
        ByteArrayInputStream b = new ByteArrayInputStream(ByteBuffer.allocate(4).putInt(testValue).array());
        log.debug("b:::::::::::::{}", b.toString().getBytes(StandardCharsets.UTF_8));

        SmsGateway gateway = getSmsGateWay();
        JunitTestUtils.setValue(gateway, "mInputStream", b);
        log.debug("b:::::::::::::{}", b.toString().getBytes(StandardCharsets.UTF_8));
        int result = ReflectionTestUtils.invokeMethod(gateway, "readBufferToInt", 4);

        // length == 0
        ByteArrayInputStream b0 = new ByteArrayInputStream(ByteBuffer.allocate(0).array());
        JunitTestUtils.setValue(gateway, "mInputStream", b0);
        int result0 = ReflectionTestUtils.invokeMethod(gateway, "readBufferToInt", 4);
        assertEquals(-1, result0);

        //String
        ByteArrayInputStream bs = new ByteArrayInputStream(ByteBuffer.allocate(4).put("CDEF".getBytes()).array());
        JunitTestUtils.setValue(gateway, "mInputStream", bs);
        String result1 = ReflectionTestUtils.invokeMethod(gateway, "readBufferToString", 4);
        assertEquals("CDEF", result1);

        // length == 0
        ByteArrayInputStream bs0 = new ByteArrayInputStream(ByteBuffer.allocate(0).array());
        JunitTestUtils.setValue(gateway, "mInputStream", bs0);
        String result2 = ReflectionTestUtils.invokeMethod(gateway, "readBufferToString", 4);
        assertEquals("", result2);
    }

    //readBufferToString
    @Test
    void test_03_S() {

        SmsGateway gateway = getSmsGateWay();
        //String
        ByteArrayInputStream bs = new ByteArrayInputStream(ByteBuffer.allocate(4).put("CDEF".getBytes()).array());
        JunitTestUtils.setValue(gateway, "mInputStream", bs);
        String result1 = ReflectionTestUtils.invokeMethod(gateway, "readBufferToString", 4);
        assertEquals("CDEF", result1);

        // length == 0
        ByteArrayInputStream bs0 = new ByteArrayInputStream(ByteBuffer.allocate(0).array());
        JunitTestUtils.setValue(gateway, "mInputStream", bs0);
        String result2 = ReflectionTestUtils.invokeMethod(gateway, "readBufferToString", 4);
        assertEquals("", result2);
    }


    //readHeader
    @Test
    void test_04_() throws InterruptedException {

        int BIND_ACK = 1;
        int DELIVER_ACK = 3;
        int REPORT = 4;
        int LINK_RECV = 7;
        int result = 0;

        SmsGateway gateway = getSmsGateWay();
        assertTrue(gateway.isBind());

        //BIND_ACK
        JunitTestUtils.setValue(gateway, "mInputStream"
                , new ByteArrayInputStream(ByteBuffer.allocate(8).putInt(BIND_ACK).putInt(result).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");

        JunitTestUtils.setValue(gateway, "mInputStream"
                , new ByteArrayInputStream(ByteBuffer.allocate(8).putInt(BIND_ACK).putInt(1).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        assertFalse(gateway.isBind());

        thread.sleep(4000); //connectGateway()가 실행되는 시간을 벌기 위해 RECONNECT_TERM 만큼 지연

        //DELIVER_ACK
        JunitTestUtils.setValue(gateway, "mInputStream"
                , new ByteArrayInputStream(ByteBuffer.allocate(8).putInt(DELIVER_ACK).putInt(result).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        String mResult = (String) ReflectionTestUtils.getField(gateway, "mResult");
        assertEquals("0000", mResult);

        JunitTestUtils.setValue(gateway, "mInputStream"
                , new ByteArrayInputStream(ByteBuffer.allocate(8).putInt(DELIVER_ACK).putInt(1).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        mResult = (String) ReflectionTestUtils.getField(gateway, "mResult");
        assertEquals("1500", mResult);

        gateway.clearResult();
        JunitTestUtils.setValue(gateway, "mInputStream"
                , new ByteArrayInputStream(ByteBuffer.allocate(8).putInt(DELIVER_ACK).putInt(2).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        mResult = (String) ReflectionTestUtils.getField(gateway, "mResult");
        assertEquals("", mResult);

        //LINK_RECV
        JunitTestUtils.setValue(gateway, "mInputStream"
                , new ByteArrayInputStream(ByteBuffer.allocate(4).putInt(LINK_RECV).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");

        //Report
        String str32 = "01234567890123456789012345678901";
        String str20 = "01234567890123456789";
        String str12 = "012345678901";
        JunitTestUtils.setValue(gateway, "mInputStream"
                , new ByteArrayInputStream(ByteBuffer.allocate(4+4+32+32+4+20+12)
                        .putInt(REPORT)
                        .putInt(result)
                        .put(str32.getBytes())
                        .put(str32.getBytes())
                        .putInt(0)
                        .put(str20.getBytes())
                        .put(str12.getBytes())
                        .array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
    }

    //readHeader
    @Test
    void test_04_NotBind() throws InterruptedException {

        int BIND_ACK = 1;


        SmsGateway gateway = getInvaildSmsGateWay();

        //BIND_ACK
        JunitTestUtils.setValue(gateway, "mInputStream"
                , new ByteArrayInputStream(ByteBuffer.allocate(8).putInt(BIND_ACK).putInt(1).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        assertFalse(gateway.isBind());
    }

    @Test
    void test_05_() throws InterruptedException, ExecutionException {

        SmsGateway gateway = getSmsGateWay();
        assertTrue(gateway.isBind());

        gateway.clearResult();
        JunitTestUtils.setValue(gateway, "mResult", "0000");
        Future<SmsGatewayResponseDto> dto =  gateway.getResult();
        SmsGatewayResponseDto smsGatewayResponseDto = dto.get();
        assertEquals("0000", smsGatewayResponseDto.getFlag());

        gateway.clearResult();
        JunitTestUtils.setValue(gateway, "mResult", "1500");
        Future<SmsGatewayResponseDto> dto1 =  gateway.getResult();
        SmsGatewayResponseDto smsGatewayResponseDto1 = dto1.get();
        assertEquals("시스템 장애", smsGatewayResponseDto1.getMessage());

        gateway.clearResult();
        JunitTestUtils.setValue(gateway, "mResult", "1101");
        Future<SmsGatewayResponseDto> dto2 =  gateway.getResult();
        SmsGatewayResponseDto smsGatewayResponseDto2 = dto2.get();

    }

}
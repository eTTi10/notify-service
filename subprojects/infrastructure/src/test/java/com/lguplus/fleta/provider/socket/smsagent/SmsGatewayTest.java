package com.lguplus.fleta.provider.socket.smsagent;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@ExtendWith({MockitoExtension.class})
@TestMethodOrder(MethodOrderer.MethodName.class)
class SmsGatewayTest {

    static NettySmsAgentServer server;
    static Thread thread;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 8999;

    @Mock
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Mock
    MessageListenerContainer smsListenerContainer;

    String id = "@id";
    String password = "@password";

    @BeforeAll
    static void setUpAll() {
        server = new NettySmsAgentServer();
        thread = new Thread(() -> {
            server.runServer(SERVER_PORT);
        });
        thread.start();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(4000));
    }

    @AfterAll
    static void setUpClose() {
        server.stopServer();
    }

    @BeforeEach
    void setUp() {
    }

    SmsGateway getSmsGateWay() {
        return new SmsGateway(SERVER_IP, SERVER_PORT + "", id, password, kafkaListenerEndpointRegistry);
    }

    SmsGateway getInvaildSmsGateWay() {
        return new SmsGateway(SERVER_IP, "1" + SERVER_PORT, id, password, kafkaListenerEndpointRegistry);
    }

    @Test
    void test_01() throws Exception {
        SmsGateway gateway = getSmsGateWay();

        int port = gateway.getPort();
        assertEquals(SERVER_PORT, port);

        SmsGateway spy_gw = spy(gateway);
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(4000));
        assertTrue(spy_gw.getBindState());

        spy_gw.sendMessage("01041112222", "01041113333", "callback", "test", 1);

        //reconnect
        Field reconnectTermField = gateway.getClass().getDeclaredField("RECONNECT_TERM");
        reconnectTermField.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(reconnectTermField, reconnectTermField.getModifiers() & ~Modifier.FINAL);
        reconnectTermField.setInt(null, 3000);
        ReflectionTestUtils.invokeMethod(spy_gw, "connectGateway");
        assertTrue(spy_gw.getBindState());

        //checkLink
        Field linkCheckTermField = gateway.getClass().getDeclaredField("LINK_CHECK_TERM");
        linkCheckTermField.setAccessible(true);
        modifiers.setInt(linkCheckTermField, linkCheckTermField.getModifiers() & ~Modifier.FINAL);
        linkCheckTermField.setInt(null, 1000);
        ReflectionTestUtils.invokeMethod(spy_gw, "checkLink");

        //sendReport
        ReflectionTestUtils.invokeMethod(spy_gw, "sendReport");
    }

    //Invalid Socket Port
    @Test
    void test_08() {
        SmsGateway gateway = getInvaildSmsGateWay();
        assertFalse(gateway.getBindState());
    }

    //readBufferToString
    @Test
    void test_00() {
        int testValue = 10;
        ByteArrayInputStream b1 = new ByteArrayInputStream(ByteBuffer.allocate(4).putInt(testValue).array());

        SmsGateway gateway = getSmsGateWay();
        ReflectionTestUtils.setField(gateway, "mInputStream", b1);
        int result = ReflectionTestUtils.invokeMethod(gateway, "readBufferToInt", 4);
        //        assertEquals(testValue, result);

        // length == 0
        ByteArrayInputStream b0 = new ByteArrayInputStream(ByteBuffer.allocate(0).array());
        ReflectionTestUtils.setField(gateway, "mInputStream", b0);
        int result0 = ReflectionTestUtils.invokeMethod(gateway, "readBufferToInt", 4);
        assertEquals(-1, result0);

    }

    //readBufferToString
    @Test
    void test_00_S() {

        SmsGateway gateway = getSmsGateWay();
        //String
        ByteArrayInputStream bss = new ByteArrayInputStream(ByteBuffer.allocate(4).put("CDEF".getBytes()).array());
        ReflectionTestUtils.setField(gateway, "mInputStream", bss);
        String result1 = ReflectionTestUtils.invokeMethod(gateway, "readBufferToString", 4);
        //        assertEquals("CDEF", result1);

        // length == 0
        ByteArrayInputStream bs0 = new ByteArrayInputStream(ByteBuffer.allocate(0).array());
        ReflectionTestUtils.setField(gateway, "mInputStream", bs0);
        String result2 = ReflectionTestUtils.invokeMethod(gateway, "readBufferToString", 4);
        assertEquals("", result2);
    }


    //readHeader
    @Test
    void test_04() throws InterruptedException {

        int BIND_ACK = 1;
        int DELIVER_ACK = 3;
        int REPORT = 4;
        int LINK_RECV = 7;
        int result = 0;

        SmsGateway gateway = getSmsGateWay();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(4000));
        assertTrue(gateway.getBindState());

        //BIND_ACK
        ReflectionTestUtils.setField(gateway, "mInputStream"
            , new ByteArrayInputStream(ByteBuffer.allocate(28).putInt(BIND_ACK).putInt(20).putInt(result).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");

        ReflectionTestUtils.setField(gateway, "mInputStream"
            , new ByteArrayInputStream(ByteBuffer.allocate(28).putInt(BIND_ACK).putInt(20).putInt(1).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        assertFalse(gateway.getBindState());

        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(4000)); //connectGateway()가 실행되는 시간을 벌기 위해 RECONNECT_TERM 만큼 지연

        //DELIVER_ACK
        ReflectionTestUtils.setField(gateway, "mInputStream"
            , new ByteArrayInputStream(ByteBuffer.allocate(80).putInt(DELIVER_ACK).putInt(72).putInt(result).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        String mResult = (String) ReflectionTestUtils.getField(gateway, "mResult");
        assertEquals("0000", mResult);

        ReflectionTestUtils.setField(gateway, "mInputStream"
            , new ByteArrayInputStream(ByteBuffer.allocate(80).putInt(DELIVER_ACK).putInt(72).putInt(1).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        mResult = (String) ReflectionTestUtils.getField(gateway, "mResult");
        assertEquals("1500", mResult);

        gateway.clearResult();
        ReflectionTestUtils.setField(gateway, "mInputStream"
            , new ByteArrayInputStream(ByteBuffer.allocate(80).putInt(DELIVER_ACK).putInt(72).putInt(2).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        mResult = (String) ReflectionTestUtils.getField(gateway, "mResult");
        assertEquals("", mResult);

        //LINK_RECV
        ReflectionTestUtils.setField(gateway, "mInputStream"
            , new ByteArrayInputStream(ByteBuffer.allocate(8).putInt(LINK_RECV).putInt(0).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");

        //Report
        String str32 = "01234567890123456789012345678901";
        String str20 = "01234567890123456789";
        String str12 = "012345678901";
        ReflectionTestUtils.setField(gateway, "mInputStream"
            , new ByteArrayInputStream(ByteBuffer.allocate(4 + 4 + 4 + 32 + 32 + 4 + 20 + 12)
                .putInt(REPORT)
                .putInt(4 + 32 + 32 + 4 + 20 + 12)
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
    void test_07_NotBind() throws InterruptedException {

        int BIND_ACK = 1;

        SmsGateway gateway = getInvaildSmsGateWay();

        //BIND_ACK
        ReflectionTestUtils.setField(gateway, "mInputStream"
            , new ByteArrayInputStream(ByteBuffer.allocate(28).putInt(BIND_ACK).putInt(20).putInt(1).array()));
        ReflectionTestUtils.invokeMethod(gateway, "readHeader");
        assertFalse(gateway.getBindState());
    }

    @Test
    void test_05() throws InterruptedException, ExecutionException, IOException {

        SmsGateway gateway = getSmsGateWay();
        SmsGateway spy_gw = spy(gateway);
        assertTrue(gateway.getBindState());

        gateway.clearResult();
        ReflectionTestUtils.setField(gateway, "mResult", "0000");
        Future<SmsGatewayResponseDto> dto = gateway.getResult();
        SmsGatewayResponseDto smsGatewayResponseDto = dto.get();
        assertEquals("0000", smsGatewayResponseDto.getFlag());
        spy_gw.sendMessage("01041112222", "01041113333", "callback", "test", 1);

        gateway.clearResult();
        ReflectionTestUtils.setField(gateway, "mResult", "1500");
        Future<SmsGatewayResponseDto> dto1 = gateway.getResult();
        SmsGatewayResponseDto smsGatewayResponseDto1 = dto1.get();
        assertEquals("시스템 장애", smsGatewayResponseDto1.getMessage());

        gateway.clearResult();
        ReflectionTestUtils.setField(gateway, "mResult", "1101");
        Future<SmsGatewayResponseDto> dto2 = gateway.getResult();
        SmsGatewayResponseDto smsGatewayResponseDto2 = dto2.get();

    }
}
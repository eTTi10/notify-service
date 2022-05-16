package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;

import com.lguplus.fleta.exception.smsagent.*;

import com.lguplus.fleta.properties.SmsAgentProps;
import com.lguplus.fleta.provider.socket.smsagent.NettySmsAgentServer;
import com.lguplus.fleta.provider.socket.smsagent.SmsGateway;
import fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class SmsAgentSocketClientTest {

    @Mock
    SmsAgentProps smsAgentProps;

    @InjectMocks
    SmsAgentSocketClient smsAgentSocketClient;

    private static final String S_FLAG = "0000";
    String sCtn = "01011112222";
    String rCtn = "01011112222";
    String message = "테스트메시지";

    static NettySmsAgentServer server;
    static Thread thread;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 8888;

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
    void setUp() throws InterruptedException {

        Map<String, String> serverMap = new HashMap<>();
        serverMap.put("index","1");
        serverMap.put("ip","localhost");
        serverMap.put("port","8888");
        serverMap.put("id","test");
        serverMap.put("password","test");

        JunitTestUtils.setValue(smsAgentProps, "servers", List.of(serverMap));
//        JunitTestUtils.setValue(smsAgentSocketClient, "agentTps", "1");

        given(smsAgentProps.findMapByIndex(anyString())).willReturn(Optional.of(serverMap));

        JunitTestUtils.setValue(smsAgentSocketClient, "agentTps", "100");
        smsAgentSocketClient.initGateway();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1500)); // mSendTerm 과 비교하는 분기를 통과하기 위해
    }

    @Test
    @DisplayName("04 SMS전송 테스트")
    void send() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        SmsGateway sGateway = new SmsGateway(SERVER_IP, "8888", id, password);
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(4000));
        LinkedList<SmsGateway> sGatewayQueue = (LinkedList<SmsGateway>)ReflectionTestUtils.getField(smsAgentSocketClient, "sGatewayQueue");
        sGatewayQueue.clear();
        sGatewayQueue.offer(sGateway);
        SmsGatewayResponseDto responseDto = smsAgentSocketClient.send(sCtn, rCtn, message);
        assertThat(responseDto.getFlag()).isEqualTo(S_FLAG);
    }

    @Test
    @DisplayName("02 PhoneNumberErrorException 테스트")
    void send_PhoneNumberErrorException() {

        SmsAgentCustomException exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, "1101111", message);
        });
        assertThat(exception.getCode()).isEqualTo("1502");

        exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, "0110111", message);
        });
        assertThat(exception.getCode()).isEqualTo("1502");
    }

    @Test
    @DisplayName("03 MsgTypeErrorException 테스트")
    void send_MsgTypeErrorException() {

        SmsAgentCustomException exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, rCtn, "가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하");
        });
        assertThat(exception.getCode()).isEqualTo("1501");
    }

    @Test
    @DisplayName("10 SystemBusyException 테스트")
    void send_SystemBusyException2() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        SmsGateway sGateway = new SmsGateway(SERVER_IP, "8888", id, password);
        LinkedList<SmsGateway> sGatewayQueue = (LinkedList<SmsGateway>)ReflectionTestUtils.getField(smsAgentSocketClient, "sGatewayQueue");
        sGatewayQueue.offer(sGateway);

        ReflectionTestUtils.setField(smsAgentSocketClient, "mSendTerm", 10000);
        SmsAgentCustomException exception = assertThrows(SmsAgentCustomException.class, () -> {
            smsAgentSocketClient.send(sCtn, rCtn, message);
        });
        assertThat(exception.getCode()).isEqualTo("1503");
    }

    @Test
    @DisplayName("06 SystemBusyException 테스트")
    void send_SystemBusyException() throws InterruptedException {

//        Thread.sleep(1500); // mSendTerm 과 비교하는 분기를 통과하기 위해
        LinkedList<SmsGateway> sGatewayQueue = (LinkedList<SmsGateway>)ReflectionTestUtils.getField(smsAgentSocketClient, "sGatewayQueue");
        sGatewayQueue.clear();
        SmsAgentCustomException exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, rCtn, message);
        });
        assertThat(exception.getCode()).isEqualTo("1503");
    }

    @Test
    @DisplayName("07 SmsAgentEtcException 테스트")
    void isBind() throws IOException, InterruptedException {

        SmsGateway sGateway = new SmsGateway(SERVER_IP, "1", id, password);
        LinkedList<SmsGateway> sGatewayQueue = (LinkedList<SmsGateway>)ReflectionTestUtils.getField(smsAgentSocketClient, "sGatewayQueue");
        sGatewayQueue.clear();
        sGatewayQueue.offer(sGateway);

        SmsAgentCustomException exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, rCtn, message);
        });
        assertThat(exception.getCode()).isEqualTo("1500");
    }

    @Test
    @DisplayName("09 SystemErrorException 테스트")
    void send_SystemErrorException() throws IOException, InterruptedException {

        SmsGateway fakeGateway = spy(new SmsGateway(SERVER_IP, "8888", id, password));
        doThrow(new IOException()).when(fakeGateway).sendMessage(anyString(), anyString(), anyString(), anyString(), anyInt());
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(4000));
        LinkedList<SmsGateway> sGatewayQueue = (LinkedList<SmsGateway>)ReflectionTestUtils.getField(smsAgentSocketClient, "sGatewayQueue");
        sGatewayQueue.clear();
        sGatewayQueue.offer(fakeGateway);

        SmsAgentCustomException exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, rCtn, message);
        });
        assertThat(exception.getCode()).isEqualTo("9999");
    }

    @Test
    @DisplayName("11 calculateTerm_Exception 테스트")
    void calculateTerm_Exception()  {

        JunitTestUtils.setValue(smsAgentSocketClient, "agentTps", null);
        assertDoesNotThrow(smsAgentSocketClient::initGateway);
    }
}

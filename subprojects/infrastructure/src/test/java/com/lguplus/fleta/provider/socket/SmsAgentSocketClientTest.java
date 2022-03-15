package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.*;
import com.lguplus.fleta.properties.SmsAgentProps;
import com.lguplus.fleta.provider.socket.multi.NettyTcpJunitServerTest;
import com.lguplus.fleta.provider.socket.smsagent.NettySmsAgentServerTest;
import com.lguplus.fleta.provider.socket.smsagent.SmsGateway;
import fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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

    static NettySmsAgentServerTest server;
    static Thread thread;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 7777;

    String id = "@id";
    String password = "@password";

    @BeforeAll
    static void setUpAll() {
        server = new NettySmsAgentServerTest();
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
    void setUp() throws InterruptedException {

        Map<String, String> serverMap = new HashMap<>();
        serverMap.put("index","1");
        serverMap.put("ip","localhost");
        serverMap.put("port","7777");
        serverMap.put("id","test");
        serverMap.put("password","test");

        JunitTestUtils.setValue(smsAgentProps, "servers", List.of(serverMap));
        JunitTestUtils.setValue(smsAgentSocketClient, "agentTps", "1");

        given(smsAgentProps.findMapByIndex(anyString())).willReturn(Optional.of(serverMap));

    }

    @Test
    @DisplayName("04 SMS전송 테스트")
    void send() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        smsAgentSocketClient.initGateway();
        Thread.sleep(1500); // mSendTerm 과 비교하는 분기를 통과하기 위해
        SmsGatewayResponseDto responseDto = smsAgentSocketClient.send(sCtn, rCtn, message);
        assertThat(responseDto.getFlag().equals(S_FLAG));
    }

    @Test
    @DisplayName("02 PhoneNumberErrorException 테스트")
    void send_PhoneNumberErrorException() {

        Exception exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, "1101111", message);
        });

        exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, "0110111", message);
        });

    }

    @Test
    @DisplayName("03 MsgTypeErrorException 테스트")
    void send_MsgTypeErrorException() {

        Exception exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, rCtn, "가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하");
        });
    }

    @Test
    @DisplayName("10 SystemBusyException 테스트")
    void send_SystemBusyException2() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        smsAgentSocketClient.initGateway();
        smsAgentSocketClient.mSendTerm = 10000;
        Exception exception = assertThrows(SmsAgentCustomException.class, () -> {
            smsAgentSocketClient.send(sCtn, rCtn, message);
        });
    }

    @Test
    @DisplayName("06 SystemBusyException 테스트")
    void send_SystemBusyException() throws InterruptedException {

//        Thread.sleep(1500); // mSendTerm 과 비교하는 분기를 통과하기 위해
        SmsAgentSocketClient.sGatewayQueue.clear();
        Exception exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, rCtn, message);
        });
    }

    @Test
    @DisplayName("07 SmsAgentEtcException 테스트")
    void isBind() throws IOException, InterruptedException {

        smsAgentSocketClient.initGateway();
        server.stopServer();
        Thread.sleep(1500); // mSendTerm 과 비교하는 분기를 통과하기 위해
        Exception exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, rCtn, message);
        });
    }

    @Test
    @DisplayName("09 SystemErrorException 테스트")
    void send_SystemErrorException() throws IOException, InterruptedException {

        SmsAgentSocketClient.sGatewayQueue.clear();
        SmsGateway fakeGateway = new SmsGateway(SERVER_IP, "1", id, password);
        SmsAgentSocketClient.sGatewayQueue.offer(fakeGateway);
        Thread.sleep(1500); // mSendTerm 과 비교하는 분기를 통과하기 위해
        Exception exception = assertThrows(SmsAgentCustomException.class, () -> {

            smsAgentSocketClient.send(sCtn, rCtn, message);
        });
    }

    @Test
    @DisplayName("08 calculateTerm_Exception 테스트")
    void calculateTerm_Exception()  {

        JunitTestUtils.setValue(smsAgentSocketClient, "agentTps", null);
        smsAgentSocketClient.initGateway();
    }


}
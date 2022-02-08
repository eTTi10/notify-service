package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.data.dto.response.SendSmsResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.SmsAgentEtcException;
import com.lguplus.fleta.properties.SmsAgentProps;
import com.lguplus.fleta.provider.socket.smsagent.SmsGateway;
import fleta.util.JunitTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SmsAgentSocketClientTest {

    @Mock
    SmsAgentProps smsAgentProps;

    @Mock
    SmsGateway smsGateway;

    @InjectMocks
    SmsAgentSocketClient smsAgentSocketClient;

    @BeforeEach
    void setUp() {

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

    String sFlag = "0000";
    String sCtn = "01011112222";
    String rCtn = "01011112222";
    String message = "테스트메시지";

    @Test
    @DisplayName("SMS전송 테스트")
    void send() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        smsAgentSocketClient.initGateway();

        Thread.sleep(1500);
        SmsGatewayResponseDto smsGatewayResponseDto = smsAgentSocketClient.send(sCtn, rCtn, message);
        assertThat(smsGatewayResponseDto.getFlag().equals(sFlag));
    }
}
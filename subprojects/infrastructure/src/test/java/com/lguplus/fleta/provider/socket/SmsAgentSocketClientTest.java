package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.properties.SmsAgentProps;
import com.lguplus.fleta.provider.socket.smsagent.SmsGateway;
import fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(SpringExtension.class)
class SmsAgentSocketClientTest {

    @Mock
    SmsAgentProps smsAgentProps;

    @Mock
    SmsGateway smsGateway;

    @InjectMocks
    SmsAgentSocketClient smsAgentSocketClient;

    private static final String S_FLAG = "0000";
    String sCtn = "01011112222";
    String rCtn = "01011112222";
    String message = "테스트메시지";

    @BeforeEach
    void setUp() throws InterruptedException {

        Map<String, String> serverMap = new HashMap<>();
        serverMap.put("index","1");
        serverMap.put("ip","localhost");
        serverMap.put("port","7777");
        serverMap.put("id","test");
        serverMap.put("password","test");

        JunitTestUtils.setValue(smsAgentProps, "servers", List.of(serverMap));
//        JunitTestUtils.setValue(smsAgentSocketClient, "agentTps", "1");

        given(smsAgentProps.findMapByIndex(anyString())).willReturn(Optional.of(serverMap));

        smsAgentSocketClient.initGateway();
        Thread.sleep(1500); // mSendTerm 과 비교하는 분기를 통과하기 위해
    }

    @Test
    @DisplayName("SMS전송 테스트")
    void send() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        SmsGatewayResponseDto responseDto = smsAgentSocketClient.send(sCtn, rCtn, message);
        assertThat(responseDto.getFlag().equals(S_FLAG));
    }
}
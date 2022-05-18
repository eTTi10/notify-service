package com.lguplus.fleta.provider.socket.smsagent;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.SmsAgentCustomException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith({MockitoExtension.class})
@TestMethodOrder(MethodOrderer.MethodName.class)
class SmsGatewayBindTaskTest {

    @Mock
    SmsGateway smsGateway;

    SmsGateway.BindTimerTask bindTimerTask;

    SmsGateway.LinkTimerTask linkTimerTask;

    SmsGateway.ErrorTimerTask errorTimerTask;

    @Test
    @DisplayName("BindTimerTask 테스트")
    void test_01() throws IOException {

        doNothing().when(smsGateway).checkLink();

        bindTimerTask = new SmsGateway.BindTimerTask(smsGateway);
        assertDoesNotThrow(bindTimerTask::run);

        doThrow(IOException.class).when(smsGateway).checkLink();
        assertDoesNotThrow(bindTimerTask::run);
    }

    @Test
    @DisplayName("LinkTimerTask 테스트")
    void test_02() throws IOException {

        ReflectionTestUtils.setField(smsGateway, "isLinked", true);
        linkTimerTask = new SmsGateway.LinkTimerTask(smsGateway);
        assertDoesNotThrow(linkTimerTask::run);

        doNothing().when(smsGateway).connectGateway();
        ReflectionTestUtils.setField(smsGateway, "isLinked", false);
        linkTimerTask = new SmsGateway.LinkTimerTask(smsGateway);
        assertDoesNotThrow(linkTimerTask::run);

    }

    @Test
    @DisplayName("ErrorTimerTask 테스트")
    void test_03() throws IOException {

        ReflectionTestUtils.setField(smsGateway, "mResult", "");
        errorTimerTask = new SmsGateway.ErrorTimerTask(smsGateway);
        assertDoesNotThrow(errorTimerTask::run);

        ReflectionTestUtils.setField(smsGateway, "mResult", "0000");
        errorTimerTask = new SmsGateway.ErrorTimerTask(smsGateway);
        assertDoesNotThrow(errorTimerTask::run);

    }
}
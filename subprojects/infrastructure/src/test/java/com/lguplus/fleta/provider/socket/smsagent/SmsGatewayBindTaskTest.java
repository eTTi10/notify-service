package com.lguplus.fleta.provider.socket.smsagent;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.SmsAgentCustomException;
import fleta.util.JunitTestUtils;
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
import static org.mockito.Mockito.spy;

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
    void test_01() throws IOException {

        given(smsGateway.checkLink()).willReturn(true);

        bindTimerTask = new SmsGateway.BindTimerTask(smsGateway);
        bindTimerTask.run();

        given(smsGateway.checkLink()).willThrow(new IOException());
        bindTimerTask.run();
    }

    @Test
    void test_02() throws IOException {

//        given(smsGateway.checkLink()).willReturn(true);
//
        JunitTestUtils.setValue(smsGateway, "isLinked", true);
        linkTimerTask = new SmsGateway.LinkTimerTask(smsGateway);
        linkTimerTask.run();

        given(smsGateway.connectGateway()).willReturn(true);
        JunitTestUtils.setValue(smsGateway, "isLinked", false);
        linkTimerTask = new SmsGateway.LinkTimerTask(smsGateway);
        linkTimerTask.run();

    }

    @Test
    void test_03() throws IOException {

        JunitTestUtils.setValue(smsGateway, "mResult", "");
        errorTimerTask = new SmsGateway.ErrorTimerTask(smsGateway);
        errorTimerTask.run();

        JunitTestUtils.setValue(smsGateway, "mResult", "0000");
        errorTimerTask = new SmsGateway.ErrorTimerTask(smsGateway);
        errorTimerTask.run();

    }
}
package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.inner.SmsAgentRequestDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.SmsAgentEtcException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * SmsAgentDomainServiceTest 만으로는 Test Coverage 요구치를 충족할 수 없어
 * SmsSender.retryIfChecked 함수에 대해서 JUnit Test Case 추가 작성.
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class SmsSenderTest {

    @Mock
    SmsAgentDomainService smsAgentDomainService;

    @Test
    void testRetryIfCheckedWithNoRetry() {

        SmsGatewayResponseDto response1 = SmsGatewayResponseDto.builder().flag("0000").build();
        SmsGatewayResponseDto response2 = SmsGatewayResponseDto.builder().flag("0001").build();

        SmsSender smsSender = new SmsSender((sender, receiver, text) -> response1, smsAgentDomainService);
        ReflectionTestUtils.setField(smsSender, "sleepTime", 0);

        SmsGatewayResponseDto result = ReflectionTestUtils.invokeMethod(smsSender, "retryIfChecked", null, SmsSender.CheckRetryType.NO_RETRY, response2);
        assertThat(result.getFlag()).isEqualTo(response2.getFlag());
    }

    @Test
    void testRetryIfCheckedWithRetryAndSystemErrorCountGreaterThanRetryCount() {

        SmsGatewayResponseDto response1 = SmsGatewayResponseDto.builder().flag("0000").build();
        SmsGatewayResponseDto response2 = SmsGatewayResponseDto.builder().flag("0001").build();

        SmsSender smsSender = new SmsSender((sender, receiver, text) -> response1, smsAgentDomainService);
        ReflectionTestUtils.setField(smsSender, "systemEr", 2);
        ReflectionTestUtils.setField(smsSender, "retry", 1);
        ReflectionTestUtils.setField(smsSender, "sleepTime", 0);

        SmsGatewayResponseDto result = ReflectionTestUtils.invokeMethod(smsSender, "retryIfChecked", null, SmsSender.CheckRetryType.RETRY_CAUSE_BUSY, response2);
        assertThat(result.getFlag()).isEqualTo(response2.getFlag());
    }

    @Test
    void testRetryIfCheckedWithRetryAndSystemErrorCountLessThanRetryCountAndBusyErrorCountGreaterThenBusyRetryCount() {

        SmsGatewayResponseDto response1 = SmsGatewayResponseDto.builder().flag("0000").build();
        SmsGatewayResponseDto response2 = SmsGatewayResponseDto.builder().flag("0001").build();

        SmsSender smsSender = new SmsSender((sender, receiver, text) -> response1, smsAgentDomainService);
        ReflectionTestUtils.setField(smsSender, "systemEr", 1);
        ReflectionTestUtils.setField(smsSender, "retry", 2);
        ReflectionTestUtils.setField(smsSender, "busyEr", 2);
        ReflectionTestUtils.setField(smsSender, "busyRetry", 1);
        ReflectionTestUtils.setField(smsSender, "sleepTime", 0);

        SmsGatewayResponseDto result = ReflectionTestUtils.invokeMethod(smsSender, "retryIfChecked", null, SmsSender.CheckRetryType.RETRY_CAUSE_BUSY, response2);
        assertThat(result.getFlag()).isEqualTo(response2.getFlag());
    }

    @Test
    void testRetryIfCheckedWithRetryAndSystemErrorCountLessThanRetryCountAndBusyErrorCountLessThenBusyRetryCount() {

        SmsAgentRequestDto request = SmsAgentRequestDto.builder().build();
        SmsGatewayResponseDto response1 = SmsGatewayResponseDto.builder().flag("0000").build();
        SmsGatewayResponseDto response2 = SmsGatewayResponseDto.builder().flag("0001").build();

        SmsSender smsSender = new SmsSender((sender, receiver, text) -> response1, smsAgentDomainService);
        ReflectionTestUtils.setField(smsSender, "systemEr", 1);
        ReflectionTestUtils.setField(smsSender, "retry", 2);
        ReflectionTestUtils.setField(smsSender, "busyEr", 1);
        ReflectionTestUtils.setField(smsSender, "busyRetry", 2);
        ReflectionTestUtils.setField(smsSender, "sleepTime", 0);

        SmsGatewayResponseDto result = ReflectionTestUtils.invokeMethod(smsSender, "retryIfChecked", request, SmsSender.CheckRetryType.RETRY_CAUSE_BUSY, response2);
        assertThat(result.getFlag()).isEqualTo(response1.getFlag());
    }

    @Test
    void testRetryIfCheckedInterruptedException() {

        SmsAgentRequestDto request = SmsAgentRequestDto.builder().build();
        SmsGatewayResponseDto response1 = SmsGatewayResponseDto.builder().flag("0000").build();
        SmsGatewayResponseDto response2 = SmsGatewayResponseDto.builder().flag("0001").build();

        SmsSender smsSender = new SmsSender((sender, receiver, text) -> response1, smsAgentDomainService);
        ReflectionTestUtils.setField(smsSender, "systemEr", 1);
        ReflectionTestUtils.setField(smsSender, "retry", 2);
        ReflectionTestUtils.setField(smsSender, "busyEr", 1);
        ReflectionTestUtils.setField(smsSender, "busyRetry", 2);
        ReflectionTestUtils.setField(smsSender, "sleepTime", 10000);

        Thread thread = new Thread(() ->
            assertThrows(SmsAgentEtcException.class, () ->
                    ReflectionTestUtils.invokeMethod(smsSender, "retryIfChecked", request, SmsSender.CheckRetryType.RETRY_CAUSE_BUSY, response2))
        );
        thread.start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        thread.interrupt();
    }
}
package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.NotifySmsRuntimeException;
import com.lguplus.fleta.service.smsagent.SmsAgentDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class SmsServiceTest {

    private static final String SUCCESS_CODE = "0000";

    @InjectMocks
    SmsService smsService;

    @Mock
    SmsAgentDomainService smsAgentDomainService;

    @BeforeEach
    void setUp() {

        smsService = new SmsService(smsAgentDomainService);

    }

    @Test
    @DisplayName("SMS 발송 성공 확인")
    void whenRequestSms_thenReturnSuccess() {
        SmsGatewayResponseDto smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                .flag("0000").build();

        // given
        given(smsAgentDomainService.sendSmsCode(any())).willReturn(smsGatewayResponseDto);

        // mock object
        SendSmsCodeRequestDto request = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        // when
        SuccessResponseDto responseDto = smsService.sendSmsCode(request);

        // then
        assertThat(responseDto.getFlag()).isEqualTo(SUCCESS_CODE);
    }

    @Test
    @DisplayName("SMS 발송이 실패일 경우 예외처리가 되는지 확인")
    void whenFailCondition_thenReturnExcepetion() {
        // mock object
        SendSmsCodeRequestDto request = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        Exception exception = assertThrows(NotifySmsRuntimeException.class, () -> {
            smsService.sendSmsCode(request);
        });

        assertThat(exception.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.NotifySmsRuntimeException");
    }
}
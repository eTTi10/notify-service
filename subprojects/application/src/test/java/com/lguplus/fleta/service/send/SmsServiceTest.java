package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SendSmsResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.service.smsagent.SmsAgentDomainService;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SmsServiceTest {

    private static final String SUCCESS_CODE = "0000";

    SendSmsCodeRequestDto request;

    @InjectMocks
    SmsService smsService;

    @Mock
    SmsAgentDomainService smsAgentDomainService;

    @BeforeEach
    void setUp() {

        //        smsService = new SmsService(smsAgentDomainService);

        // mock object
        request = SendSmsCodeRequestDto.builder()
            .saId("M15030600001")
            .stbMac("v150.3060.0001")
            .smsCd("S001")
            .ctn("01051603997")
            .replacement("http://google.com/start/we09gn2ks")
            .build();

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
        SendSmsResponseDto responseDto = smsService.sendSmsCode(request);

        // then
        assertThat(responseDto.getFlag()).isEqualTo(SUCCESS_CODE);
    }

}
package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.service.smsagent.SmsAgentDomainService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class SmsServiceTest {


    @InjectMocks
    SmsService smsService;

    @Mock
    SmsAgentDomainService smsAgentDomainService;

    @BeforeEach
    void setUp() {

        smsService = new SmsService(smsAgentDomainService);

    }

    @Test
    void sendSmsCode() {

        SmsGatewayResponseDto smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                .flag("0000")
                .message("성공")
                .build();

        given(smsService.sendSmsCode(any())).willReturn(smsGatewayResponseDto);

        SendSmsCodeRequestDto request = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        SmsGatewayResponseDto responseDto = smsService.sendSmsCode(request);

        Assertions.assertThat(responseDto.getFlag()).isEqualTo("0000");


    }
}
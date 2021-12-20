package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsAgentService {

    private final SmsAgentDomainService smsAgentDomainService;

    public SmsGatewayResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) {

        return smsAgentDomainService.sendSms(sendSmsRequestDto);
    }

    public SmsGatewayResponseDto sendSmsCode(SendSmsCodeRequestDto sendSmsCodeRequestDto) {

        return smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
    }
}

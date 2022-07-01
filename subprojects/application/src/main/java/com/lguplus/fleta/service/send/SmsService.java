package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SendSmsResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.service.smsagent.SmsAgentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SmsService {

    private final SmsAgentDomainService smsAgentDomainService;

    public SendSmsResponseDto sendSmsCode(SendSmsCodeRequestDto request) {

        SmsGatewayResponseDto smsGatewayResponseDto = smsAgentDomainService.sendSmsCode(request);

        return SendSmsResponseDto.builder()
            .flag(smsGatewayResponseDto.getFlag())
            .message(smsGatewayResponseDto.getMessage())
            .build();
    }

}

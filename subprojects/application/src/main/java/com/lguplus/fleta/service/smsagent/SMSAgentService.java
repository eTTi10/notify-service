package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.outer.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.request.outer.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSAgentService {

    private final SMSAgentDomainService smsAgentDomainService;

    public SuccessResponseDto sendSMS(SendSMSRequestDto sendSMSRequestDto) {

        return smsAgentDomainService.send(sendSMSRequestDto);
    }

    public SuccessResponseDto sendSMSCode(SendSMSCodeRequestDto sendSMSCodeRequestDto) {

        return smsAgentDomainService.sendSMS(sendSMSCodeRequestDto);
    }
}

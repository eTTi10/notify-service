package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SendSMSResponseDto;
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

        return smsAgentDomainService.sendSMS(sendSMSRequestDto);
    }
}

package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.service.smsagent.SMSAgentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSService {

    private final SMSAgentDomainService smsAgentDomainService;

    public SuccessResponseDto sendSMS(SendSMSRequestDto request) {


        return smsAgentDomainService.sendSMS(request);
    }


}

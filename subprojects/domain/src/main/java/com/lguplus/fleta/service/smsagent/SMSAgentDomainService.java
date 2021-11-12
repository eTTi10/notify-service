package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.outer.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.request.outer.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SMSAgentDomainService {

    public SuccessResponseDto send(SendSMSRequestDto sendSMSRequestDto) {

        log.debug("SMSAgentDomainService.sendSMS() - {}:{}", "SMS발송 처리", sendSMSRequestDto);

        return SuccessResponseDto.builder().build();
    }
    public SuccessResponseDto sendSMS(SendSMSCodeRequestDto sendSMSCodeRequestDto) {

        log.debug("SMSAgentDomainService.sendSMS() - {}:{}", "SMS발송 처리", sendSMSCodeRequestDto);

        return SuccessResponseDto.builder().build();
    }

}

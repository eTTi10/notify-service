package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.service.smsagent.SmsAgentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsAgentDomainService smsAgentDomainService;

    public SuccessResponseDto sendSmsCode(SendSmsCodeRequestDto request) {

        SuccessResponseDto responseDto = smsAgentDomainService.sendSmsCode(request);
        return responseDto;
    }

}

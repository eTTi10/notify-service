package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.NotifySmsRuntimeException;
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

        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(request);

        if (responseDto != null && responseDto.getFlag().equals("0000")) {
            return SuccessResponseDto.builder().build();
        }
        else {
            throw new NotifySmsRuntimeException();
        }
    }

}

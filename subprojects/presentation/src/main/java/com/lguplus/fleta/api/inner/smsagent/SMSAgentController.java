package com.lguplus.fleta.api.inner.smsagent;

import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SendSMSResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendSMSVo;
import com.lguplus.fleta.service.smsagent.SMSAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SMSAgentController {

    private final SMSAgentService smsAgentService;

    @PostMapping("/smsagent/sms")
    public SuccessResponseDto sendSms(@Valid SendSMSVo request) {

        log.debug("SMSAgentController.sendSms() - {}:{}", "SMS발송 처리", request);

        return SuccessResponseDto.builder().build();
    }

    @PostMapping("/smsagent/smsCode")
    public SuccessResponseDto sendSmsCode(@Valid SendSMSVo request) {

        log.debug("SMSAgentController.sendSmsCode() - {}:{}", "SMS발송 처리", request);

        SendSMSRequestDto requestDto = request.convert();

        return smsAgentService.sendSMS(requestDto);
    }
}

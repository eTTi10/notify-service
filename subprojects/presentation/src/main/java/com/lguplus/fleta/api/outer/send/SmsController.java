package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.api.inner.smsagent.SMSAgentController;
import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendSMSVo;
import com.lguplus.fleta.service.send.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SmsController {

    private final SMSService smsService;

    @PostMapping("/mims/sendSms")
    public SuccessResponseDto setPayment(@Valid SendSMSVo request) {

        log.debug("SmsController.setPayment() - {}:{}", "SMS발송 요청", request);

        SendSMSRequestDto requestDto = request.convert();

        return smsService.sendSMS(requestDto);
    }

}

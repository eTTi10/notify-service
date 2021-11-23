package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.data.dto.request.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendSMSCodeVo;
import com.lguplus.fleta.service.send.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@Slf4j
@RequiredArgsConstructor
@RestController
public class SmsController {

    private final SMSService smsService;

    @PostMapping("/mims/sendSms")
    public SuccessResponseDto setPayment(@Valid SendSMSCodeVo request) {

//        log.debug("sendTime - {}", sendTime);

        SendSMSCodeRequestDto requestDto = request.convert();

        return smsService.sendSMSCode(requestDto);
    }

}

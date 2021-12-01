package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.data.vo.SendSmsCodeVo;
import com.lguplus.fleta.service.send.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@Slf4j
@RequiredArgsConstructor
@RestController
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/mims/sendSms")
    public SmsGatewayResponseDto sendSmsCode(@Valid SendSmsCodeVo request) {

//        log.debug("sendTime - {}", sendTime);

        SendSmsCodeRequestDto requestDto = request.convert();

        return smsService.sendSmsCode(requestDto);
    }

}

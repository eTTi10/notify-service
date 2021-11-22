package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendMMSVo;
import com.lguplus.fleta.service.send.MMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MmsController {

    private final MMSService smsService;

    @PostMapping("/mims/sendMms")
    public SuccessResponseDto setPayment(@Valid SendMMSVo request) {

        log.debug("SmsController.setPayment() - {}:{}", "MMS발송 요청", request);

        SendMMSRequestDto requestDto = request.convert();

        return smsService.sendMMS(requestDto);
    }

}

package com.lguplus.fleta.api.inner.mmsagent;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendMMSVo;
import com.lguplus.fleta.service.mmsagent.MMSAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MMSAgentController {

    private final MMSAgentService mmsAgentService;

    @PostMapping("/mmsagent/mms")
    public SuccessResponseDto sendSms(@Valid SendMMSVo request) {

        log.debug("SMSAgentController.sendMms() - {}:{}", "MMS발송 처리", request);

        return SuccessResponseDto.builder().build();
    }

    @PostMapping("/smsagent/mmsCode")
    public SuccessResponseDto sendMmsCode(@Valid SendMMSVo request) {

        log.debug("MMSAgentController.sendMmsCode() - {}:{}", "MMS발송 처리", request);

        SendMMSRequestDto requestDto = request.convert();

        return mmsAgentService.sendMMS(requestDto);
    }
}

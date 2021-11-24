package com.lguplus.fleta.api.inner.smsagent;

import com.lguplus.fleta.data.dto.request.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendSMSCodeVo;
import com.lguplus.fleta.data.vo.SendSMSVo;
import com.lguplus.fleta.service.smsagent.SMSAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Slf4j
@RequiredArgsConstructor
@RestController
public class SMSAgentController {

    @Value("${agent.no.sendtime}")
    private String sendTime;

    @PostMapping("/smsagent/sms")
    public SuccessResponseDto sendSms(@Valid SendSMSVo request) {

        log.debug("[SMSAgentController] - [{}]]", request.toString());

        SendSMSRequestDto requestDto = request.convert();

        return SuccessResponseDto.builder().build();
//        return smsAgentService.sendSMS(requestDto);
    }

    @PostMapping("/smsagent/smsCode")
    public SuccessResponseDto sendSmsCode(@Valid SendSMSCodeVo request) {

        log.debug("SMSAgentController.sendSmsCode() - {}:{}", "SMS발송 처리", request);

        SendSMSCodeRequestDto requestDto = request.convert();

        return SuccessResponseDto.builder().build();
//        return smsAgentService.sendSMSCode(requestDto);
    }


    /**
     * 삭제
     * 요구사항정의서에 없음
     * @return HttpServletRequest 결과
     */
    @RequestMapping(value = "/smsCode", method = RequestMethod.DELETE)
    public SuccessResponseDto deleteCacheSmsMsg(HttpServletRequest request) {

        return SuccessResponseDto.builder().build();
    }
}

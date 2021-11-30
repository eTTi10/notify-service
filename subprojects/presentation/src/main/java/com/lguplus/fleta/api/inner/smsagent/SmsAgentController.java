package com.lguplus.fleta.api.inner.smsagent;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendSmsCodeVo;
import com.lguplus.fleta.data.vo.SendSmsVo;
import com.lguplus.fleta.exception.smsagent.NoHttpsException;
import com.lguplus.fleta.exception.smsagent.ServerSettingInfoException;
import com.lguplus.fleta.service.smsagent.SmsAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Slf4j
@RequiredArgsConstructor
@RestController
public class SmsAgentController {

    @Value("${check.https}")
    private String propertyCheckHttps;

    private final SmsAgentService smsAgentService;

    @PostMapping("/smsagent/sms")
    public SuccessResponseDto sendSms(@Valid SendSmsVo requestVo, HttpServletRequest request) {

        log.debug("[SMSAgentController] - [{}]]", requestVo.toString());

        log.debug("[request.getRequestURI()] - [{}]]", request.getRequestURI());
        log.debug("[request.getScheme()] - [{}]]", request.getScheme());

        // Http통신 체크
        checkHttps(request);

        SendSmsRequestDto requestDto = requestVo.convert();

        return smsAgentService.sendSms(requestDto);
    }

    @PostMapping("/smsagent/smsCode")
    public SuccessResponseDto sendSmsCode(@Valid SendSmsCodeVo request) {

        log.debug("SMSAgentController.sendSmsCode() - {}:{}", "SMS발송 처리", request);

        SendSmsCodeRequestDto requestDto = request.convert();

        return smsAgentService.sendSmsCode(requestDto);
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

    /**
     * HTTPS 통신 체크
     *
     * @param request HttpServletRequest
     */
    private void checkHttps(HttpServletRequest request) {

        String checkHttps = StringUtils.defaultString(propertyCheckHttps, "1");

        if (!"0".equals(checkHttps)) {
            String protocol = request.getScheme();

            if (!"https".equalsIgnoreCase(protocol)) {

                throw new NoHttpsException("HTTPS 통신이 아닙니다");
            }
        }
    }
}

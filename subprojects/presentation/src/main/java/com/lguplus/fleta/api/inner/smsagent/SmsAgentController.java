package com.lguplus.fleta.api.inner.smsagent;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
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
@RequestMapping("/notify")
public class SmsAgentController {

    @Value("${check.https}")
    private String propertyCheckHttps;

    private final SmsAgentService smsAgentService;


    /**
     * sms전송(
     * @param requestVo
     * @param request
     * @return
     */
    @PostMapping("/smsagent/sms")
    public InnerResponseDto<SmsGatewayResponseDto> sendSms(@Valid SendSmsVo requestVo, HttpServletRequest request) {

        log.debug("[SMSAgentController] - [{}]]", requestVo.toString());

        // Http통신 체크
        checkHttps(request);

        SendSmsRequestDto requestDto = requestVo.convert();

        return InnerResponseDto.of(smsAgentService.sendSms(requestDto));
    }

    @PostMapping("/smsagent/smsCode")
    public InnerResponseDto<SmsGatewayResponseDto> sendSmsCode(@Valid SendSmsCodeVo requestVo) {

        log.debug("SMSAgentController.sendSmsCode() - {}:{}", "SMS발송 처리", requestVo);

        SendSmsCodeRequestDto requestDto = requestVo.convert();

        return InnerResponseDto.of(smsAgentService.sendSmsCode(requestDto));
    }

    /**
     * HTTPS 통신 체크
     *
     * @param request HttpServletRequest
     */
    private void checkHttps(HttpServletRequest request) {

        String checkHttps = StringUtils.defaultString(propertyCheckHttps, "1");
        log.debug("[checkHttps] - [{}]]", checkHttps);
        String protocol = request.getScheme();

        if (!"0".equals(checkHttps)) {

            if (!"https".equalsIgnoreCase(protocol)) {

                throw new NoHttpsException("HTTPS 통신이 아닙니다");
            }
        }
    }
}

package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSAgentService {

    private final SMSAgentDomainService smsAgentDomainService;

    public SuccessResponseDto sendSMS(SendSMSRequestDto sendSMSRequestDto) {

        String s_ctn = sendSMSRequestDto.getSCtn();
        String r_ctn = sendSMSRequestDto.getRCtn();
        String msg = sendSMSRequestDto.getMsg();

        log.debug("[sms] - [{}][{}][{}]", s_ctn, r_ctn, msg);

        s_ctn = s_ctn.replace("-", "").replace(".", "");
        r_ctn = r_ctn.replace("-", "").replace(".", "");


        smsAgentDomainService.send(sendSMSRequestDto);

        return SuccessResponseDto.builder().build();
    }


    public SuccessResponseDto sendSMSCode(SendSMSCodeRequestDto sendSMSCodeRequestDto) {

        smsAgentDomainService.sendSMS(sendSMSCodeRequestDto);
        return SuccessResponseDto.builder().build();
    }
}

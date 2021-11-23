package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SMSAgentDomainService {


    /**
     *
     * 단순 SMS 전송
     * @param sendSMSRequestDto SMS전송 DTO
     * @return 단건푸시등록 결과
     */
    public SuccessResponseDto send(SendSMSRequestDto sendSMSRequestDto) {

        log.debug("SMSAgentDomainService.sendSMS() - {}:{}", "SMS발송 처리", sendSMSRequestDto);

        return ;
    }

    public String selectSmsMsg(String sms_cd, Log log) {
        Map<String, String> map = null;
        try{
            map = service.callSettingApi(log);
        }catch(java.lang.Exception e){}

        if(null == map || map.size() == 0){
            log.info("[selectSmsMsg]Cannot found URL");
            return "";
        }

        return map.get(sms_cd);
    }

    /**
     * HTTPS 통신 체크
     *
     * @param request HttpServletRequest
     */
    private void checkHttps(HttpServletRequest request) {
        String checkHttps = StringUtils.defaultString(Properties.getProperty("check.https"), "1");
        CustomExceptionHandler exception = new CustomExceptionHandler();

        if (!"0".equals(checkHttps)) {
            String protocol = request.getScheme();

            if (!"https".equalsIgnoreCase(protocol)) {
                exception.setFlag(Properties.getProperty("flag.no.https"));
                exception.setMessage(Properties.getProperty("message.no.https"));
                throw exception;
            }
        }
    }



    /**
     *
     * 코드를 이용한 SMS 전송
     * @param sendSMSCodeRequestDto SMS전송 DTO
     * @return 단건푸시등록 결과
     */
    public SuccessResponseDto sendSMS(SendSMSCodeRequestDto sendSMSCodeRequestDto) {

        log.debug("SMSAgentDomainService.sendSMS() - {}:{}", "SMS발송 처리", sendSMSCodeRequestDto);

        return SuccessResponseDto.builder().build();
    }

}

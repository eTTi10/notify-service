package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSAgentService {

    private final SMSAgentDomainService smsAgentDomainService;
    private final RetryModuleDomainService retryModuleDomainService;
/*

    public SuccessResponseDto sendSMS(SendSMSRequestDto sendSMSRequestDto) {

        log.debug("[sms] - [{}]]", sendSMSRequestDto.toString());

        //#########[LOG SET]#########
        CLog cLog = new CLog(LogFactory.getLog(TAG), request);
        cLog.startLog(s_ctn, r_ctn, msg);

        CustomExceptionHandler exception = new CustomExceptionHandler();

        ResultVO resultVO = new ResultVO();

        r_ctn = r_ctn.replace("-", "").replace(".", "");
        s_ctn = s_ctn.replace("-", "").replace(".", "");

        try {
            // Http통신 체크
            checkHttps(request);

            Map<String, String> map = new HashMap<String, String>();
            map.put("s_ctn", s_ctn);
            map.put("r_ctn", r_ctn);
            map.put("msg", msg);
            CommonUtil.checkValidation(map);

            String noSendUse = Properties.getProperty("agent.no.send.use");

            if ("1".equals(noSendUse)) {
                try {
                    String noSendTime = Properties.getProperty("agent.no.sendtime");

                    if (!CommonUtil.isEmpty(noSendTime)) {
                        Calendar cal = Calendar.getInstance();
                        Calendar startCal = Calendar.getInstance();
                        Calendar endCal = Calendar.getInstance();

                        String[] noSendAry = noSendTime.split("\\|");

                        int startTime = Integer.parseInt(noSendAry[0]);
                        int endTime = Integer.parseInt(noSendAry[1]);

                        startCal.set(Calendar.HOUR_OF_DAY, startTime);
                        startCal.set(Calendar.MINUTE, 0);
                        startCal.set(Calendar.SECOND, 0);
                        startCal.set(Calendar.MILLISECOND, 0);

                        if (startTime >= endTime) {
                            endCal.add(Calendar.DAY_OF_MONTH, 1);
                        }

                        endCal.set(Calendar.HOUR_OF_DAY, endTime);
                        endCal.set(Calendar.MINUTE, 0);
                        endCal.set(Calendar.SECOND, 0);
                        endCal.set(Calendar.MILLISECOND, 0);

                        if (cal.after(startCal) && cal.before(endCal)) {
                            exception.setFlag(Properties.getProperty("flag.not_send_time"));
                            exception.setMessage(Properties.getProperty("message.not_send_time"));
                            throw exception;
                        }
                    }
                } catch (Exception e) {
                    exception.setFlag(Properties.getProperty("flag.properties"));
                    exception.setMessage(Properties.getProperty("message.properties"));
                    throw exception;
                }
            }

            resultVO = SmsProvider.send(s_ctn, r_ctn, msg);
        } catch (CustomExceptionHandler e) {
            cLog.endLog(s_ctn, r_ctn, msg, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            cLog.errorLog(s_ctn, r_ctn, msg, e.getClass().getSimpleName(), e.getMessage());
            throw new CustomExceptionHandler(e);
        }

        //#########[LOG END]#########
        cLog.endLog(s_ctn, r_ctn, msg, resultVO.getFlag());
        return resultVO;
//        return SuccessResponseDto.builder().build();
    }

    public ResultVO sendSmsCode(SmsSendVo smssendVo, boolean encryptYn, Log log) {
        return retryModuleDomainService.smsSend(smssendVo, encryptYn, log);

    }

    public SuccessResponseDto sendSMSCode(SendSMSCodeRequestDto sendSMSCodeRequestDto) {

        //#########[LOG SET]#########
        Log log = LogFactory.getLog(TAG);
        CLog cLog = new CLog(log, request);
        cLog.startLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement);

        ResultVO resultVO = new ResultVO();

        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("sms_cd", sms_cd);
            map.put("ctn", ctn);
            CommonUtil.checkValidation(map);

            String msg = selectSmsMsg(sms_cd, log);
            if(StringUtils.isEmpty(msg)){
                CustomExceptionHandler ex = new CustomExceptionHandler();
                ex.setFlag(Properties.getProperty("flag.notfound.msg"));
                ex.setMessage(Properties.getProperty("message.notfound.msg"));
                throw ex;
            }

            SmsSendVo smssendVo = new SmsSendVo();
            smssendVo.setSmsCd(sms_cd);
            smssendVo.setSmsId(ctn);
            smssendVo.setReplacement(replacement);
            smssendVo.setSmsMsg(msg);
            resultVO = service.sendSmsCode(smssendVo, false, log);
        } catch (CustomExceptionHandler e) {
            cLog.endLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            cLog.errorLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, e.getClass().getSimpleName(), e.getMessage());
            throw new CustomExceptionHandler(e);
        }

        //#########[LOG END]#########
        cLog.endLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, resultVO.getFlag());
        return resultVO;
//        return SuccessResponseDto.builder().build();
    }
*/
}

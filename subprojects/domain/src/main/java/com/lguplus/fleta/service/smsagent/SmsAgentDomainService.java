package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.exception.smsagent.NotSendTimeException;
import com.lguplus.fleta.exception.smsagent.ServerSettingInfoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 *
 *  SMSAgentDomainService 설명
 *
 *  최초작성 2021-11-23
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsAgentDomainService {

    @Value("${check.https}")
    private String propertyCheckHttps;

    @Value("${agent.no.send.use}")
    private String noSendUse;

    @Value("${agent.no.sendtime}")
    private String noSendTime;


//    @Value("${flag.no.https}")
//    private String flagNoHttps;

//    @Value("${message.no.https}")
//    private String messageNoHttps;

    private final RetryModuleDomainService retryModuleDomainService;

    public SuccessResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) {

        log.debug("[sms] - [{}]]", sendSmsRequestDto.toString());

//        ResultVO resultVO = new ResultVO();

//        try {
//            // Http통신 체크
////            checkHttps(request);
//
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("s_ctn", s_ctn);
//            map.put("r_ctn", r_ctn);
//            map.put("msg", msg);
//
//            CommonUtil.checkValidation(map);
//
//            if ("1".equals(noSendUse)) {
//                try {
//
//                    if (!noSendTime.isEmpty()) {
//                        Calendar cal = Calendar.getInstance();
//                        Calendar startCal = Calendar.getInstance();
//                        Calendar endCal = Calendar.getInstance();
//
//                        String[] noSendAry = noSendTime.split("\\|");
//
//                        int startTime = Integer.parseInt(noSendAry[0]);
//                        int endTime = Integer.parseInt(noSendAry[1]);
//
//                        startCal.set(Calendar.HOUR_OF_DAY, startTime);
//                        startCal.set(Calendar.MINUTE, 0);
//                        startCal.set(Calendar.SECOND, 0);
//                        startCal.set(Calendar.MILLISECOND, 0);
//
//                        if (startTime >= endTime) {
//                            endCal.add(Calendar.DAY_OF_MONTH, 1);
//                        }
//
//                        endCal.set(Calendar.HOUR_OF_DAY, endTime);
//                        endCal.set(Calendar.MINUTE, 0);
//                        endCal.set(Calendar.SECOND, 0);
//                        endCal.set(Calendar.MILLISECOND, 0);
//
//                        if (cal.after(startCal) && cal.before(endCal)) {
//
//                            throw new NotSendTimeException("전송 가능한 시간이 아님");
//                        }
//                    }
//                } catch (Exception e) {
//
//                    throw new ServerSettingInfoException("서버 설정 정보 오류");
//                }
//            }
//
//            SuccessResponseDto = SmsProviderDomainService.sendSmsProvider(s_ctn, r_ctn, msg);
//
//
//        } catch (CustomExceptionHandler e) {
//            cLog.endLog(s_ctn, r_ctn, msg, e.getClass().getSimpleName(), e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            cLog.errorLog(s_ctn, r_ctn, msg, e.getClass().getSimpleName(), e.getMessage());
//            throw new CustomExceptionHandler(e);
//        }
//
//        //#########[LOG END]#########
//        cLog.endLog(s_ctn, r_ctn, msg, resultVO.getFlag());
////        return resultVO;
        return SuccessResponseDto.builder()
                .build();

    }

    public SuccessResponseDto sendSmsCode(SendSmsCodeRequestDto sendSMSCodeRequestDto) {

        //#########[LOG SET]#########
//        Log log = LogFactory.getLog(TAG);
//        CLog cLog = new CLog(log, request);
//        cLog.startLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement);
//
//        ResultVO resultVO = new ResultVO();
//
//        try {
//            Map<String, String> map = new HashMap<String, String>();
//            map.put("sms_cd", sms_cd);
//            map.put("ctn", ctn);
//            CommonUtil.checkValidation(map);
//
//            String msg = selectSmsMsg(sms_cd, log);
//            if(StringUtils.isEmpty(msg)){
//                CustomExceptionHandler ex = new CustomExceptionHandler();
//                ex.setFlag(Properties.getProperty("flag.notfound.msg"));
//                ex.setMessage(Properties.getProperty("message.notfound.msg"));
//                throw ex;
//            }
//
//            SmsSendVo smssendVo = new SmsSendVo();
//            smssendVo.setSmsCd(sms_cd);
//            smssendVo.setSmsId(ctn);
//            smssendVo.setReplacement(replacement);
//            smssendVo.setSmsMsg(msg);
//            resultVO = service.sendSmsCode(smssendVo, false, log);
//        } catch (CustomExceptionHandler e) {
//            cLog.endLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, e.getClass().getSimpleName(), e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            cLog.errorLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, e.getClass().getSimpleName(), e.getMessage());
//            throw new CustomExceptionHandler(e);
//        }
//
//        //#########[LOG END]#########
//        cLog.endLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, resultVO.getFlag());
//        return resultVO;

        return SuccessResponseDto.builder().build();
    }

    public SuccessResponseDto sendSmsCode(SendSmsCodeRequestDto sendSmsCodeRequestDto, boolean encryptYn, Log log) {
        return retryModuleDomainService.smsSendCode(sendSmsCodeRequestDto, encryptYn, log);

    }


    public String selectSmsMsg(String sms_cd, Log log) {

        Map<String, String> map = null;
        try{
            map = callSettingApi(log);
        }catch(java.lang.Exception e){}

        if(null == map || map.size() == 0){
            log.info("[selectSmsMsg]Cannot found URL");
            return "";
        }

        return map.get(sms_cd);
    }


    @Cacheable(value="smsMessageCacheVaue", key="smsMessageCache")
    public Map<String,String> callSettingApi(Log log) throws Exception{


        Map<String, String> map = new HashMap<String, String>();
/*        try {
            String url = Properties.getProperty("sms.setting.url");
            String method = StringUtils.defaultIfEmpty(Properties.getProperty("sms.setting.request.method"), "GET");
            int timeout = Integer.parseInt(StringUtils.defaultIfEmpty(Properties.getProperty("sms.setting.timeout"), "5000"));
            if(StringUtils.isEmpty(url)){
                log.info("[selectSmsMsg]Cannot found URL");
                return null;
            }

            String response = TransferUtilDomainClass.callHttpClient(url, "application/json", method, "UTF-8", null, timeout, timeout);
            log.info("[callSettingApi][Call]["+response+"]");

            JSONObject jObj = (JSONObject)JSONValue.parse(response);
            JSONArray records = (JSONArray) ((JSONObject)jObj.get("result")).get("recordset");
            for(int i=0;i < records.size(); i++){
                JSONObject jo = (JSONObject)records.get(i);
                String codeId = (String) jo.get("code_id");
                String codeName = (String) jo.get("code_name");
                map.put(codeId, codeName);
            }
        } catch (Exception e) {
            log.info("[callSettingApi][Call]["+e.getClass().getName()+"]"+e.getMessage());
            throw e;
        }
*/
        return map;
    }

}

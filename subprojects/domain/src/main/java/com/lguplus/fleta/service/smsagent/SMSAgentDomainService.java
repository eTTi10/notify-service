package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
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
public class SMSAgentDomainService {

    @Value("${check.https}")
    private String propertyCheckHttps;

//    @Value("${flag.no.https}")
//    private String flagNoHttps;

//    @Value("${message.no.https}")
//    private String messageNoHttps;

    public SuccessResponseDto sendSmsCode(SendSMSCodeRequestDto sendSMSCodeRequestDto) {

        return SuccessResponseDto.builder().build();
    }
/*

    public String selectSmsMsg(String sms_cd, Log log) {

        Map<String, String> map = null;
        try{
            map = smsAgentDomainService.callSettingApi(log);
        }catch(java.lang.Exception e){}

        if(null == map || map.size() == 0){
            log.info("[selectSmsMsg]Cannot found URL");
            return "";
        }

        return map.get(sms_cd);
    }


*/
/**
     * HTTPS 통신 체크
     *
     * @param request HttpServletRequest
     *//*


    private void checkHttps(HttpServletRequest request) {

        String checkHttps = StringUtils.defaultString(propertyCheckHttps, "1");
        CustomExceptionHandler exception = new CustomExceptionHandler();

        if (!"0".equals(checkHttps)) {
            String protocol = request.getScheme();

            if (!"https".equalsIgnoreCase(protocol)) {
                exception.setFlag(flagNoHttps);
                exception.setMessage(messageNoHttps);
                throw exception;
            }
        }
    }


    @Cacheable(cacheName="smsMessageCache")
    public Map<String,String> callSettingApi(Log log) throws Exception{

        Map<String, String> map = new HashMap<String, String>();
        try {
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

        return map;
    }
*/

}

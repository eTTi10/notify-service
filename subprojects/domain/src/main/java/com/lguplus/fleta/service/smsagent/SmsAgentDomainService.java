package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
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
import java.util.List;
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

    private final CallSettingDomainClient apiClient;

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
        /*
        ASIS 히스토리
        SMSAgentController.sendSmsCode > selectSmsMsg > SmsAgentServiceImpl.callSettingApi > map = service.callSettingApi(log);
        > 코드명기반의 map전체를 리턴해서 sms_cd와 매치되는것 한개를 취득함
        > 전체를 모두 가져오는 방식이라서 tobe에서는 사용하지 않고 api에 직접적으로 sms_cd를 던져줘서 1건만 취득
         */
        CallSettingRequestDto prm = CallSettingRequestDto.builder().build();//callSettingApi파라메타
        prm.setSaId("sms");//yml파일 사용할것
        prm.setStbMac("sms");//yml파일 사용할것
        prm.setCodeId("M011");//Dto에서 받을것 ex) M011 <--- sendSmsRequestDto에 sms_cd가 보이지 않음
        prm.setSvcType("I");//yml에서 받을것
        //setting API 호출하여 메세지 등록
        CallSettingResultMapDto callSettingApi = apiClient.smsCallSettingApi(prm);
        //메세지목록 조회결과 취득
        List<CallSettingDto> settingApiList =  callSettingApi.getResult().getRecordset();
        if(callSettingApi.getResult().getTotalCount() > 0) {
            CallSettingDto settingItem = settingApiList.get(0);//취득된 1건
        }

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

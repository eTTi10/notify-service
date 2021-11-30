package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.request.inner.SmsAgentRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.NotFoundMsgException;
import com.lguplus.fleta.exception.smsagent.NotSendTimeException;
import com.lguplus.fleta.exception.smsagent.ServerSettingInfoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

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

    @Value("${check.https}")
    private String propertyCheckHttps;

    @Value("${agent.no.send.use}")
    private String agentNoSendUse;

    @Value("${agent.no.sendtime}")
    private String agentNoSendTime;

    @Value("${sms.setting.rest_url}")
    private String smsSettingRestUrl;

    @Value("${sms.setting.rest_path}")
    private String smsSettingRestPath;

    @Value("${sms.setting.request.method}")
    private String smsSettingRequestMethod;

    @Value("${sms.setting.timeout}")
    private String smsSettingTimeout;

    @Value("${sms.setting.rest_sa_id}")
    private String smsSettingRestSaId;

    @Value("${sms.setting.rest_stb_mac}")
    private String smsSettingRestStbMac;

    @Value("${sms.setting.rest_code_id}")
    private String smsSettingRestCodeId;

    @Value("${sms.setting.rest_svc_type}")
    private String smsSettingRestSvcType;

    private final RetryModuleDomainService retryModuleDomainService;
    private final CallSettingDomainClient apiClient;

    public SmsGatewayResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) {

        log.debug("[sms] - [{}]]", sendSmsRequestDto.toString());

        String s_ctn = sendSmsRequestDto.getSCtn();
        String r_ctn = sendSmsRequestDto.getRCtn();
        String msg = sendSmsRequestDto.getMsg();

        SmsGatewayResponseDto smsGatewayResponseDto = new SmsGatewayResponseDto();

        try {

            if ("1".equals(agentNoSendUse)) {

                try {

                    if (!agentNoSendTime.isEmpty()) {
                        Calendar cal = Calendar.getInstance();
                        Calendar startCal = Calendar.getInstance();
                        Calendar endCal = Calendar.getInstance();

                        String[] noSendAry = agentNoSendTime.split("\\|");

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

                            throw new NotSendTimeException("전송 가능한 시간이 아님");
                        }
                    }
                } catch (Exception e) {

                    throw new ServerSettingInfoException("서버 설정 정보 오류");
                }
            }

            smsGatewayResponseDto = SmsProviderDomainService.send(s_ctn, r_ctn, msg);


//        } catch (CustomExceptionHandler e) {
//            cLog.endLog(s_ctn, r_ctn, msg, e.getClass().getSimpleName(), e.getMessage());
//            throw e;
        } catch (Exception e) {
//            cLog.errorLog(s_ctn, r_ctn, msg, e.getClass().getSimpleName(), e.getMessage());
//            throw new CustomExceptionHandler(e);
        }

        //#########[LOG END]#########
//        cLog.endLog(s_ctn, r_ctn, msg, resultVO.getFlag());
//        return resultVO;
        return smsGatewayResponseDto;

    }

    public SmsGatewayResponseDto sendSmsCode(SendSmsCodeRequestDto sendSMSCodeRequestDto) {

        //#########[LOG SET]#########
        log.debug ("[smsCode] - {}", sendSMSCodeRequestDto.toString());
        String sms_cd = sendSMSCodeRequestDto.getSmsCd();

        SmsGatewayResponseDto smsGatewayResponseDto = new SmsGatewayResponseDto();

        try {

            String msg = selectSmsMsg(sms_cd);
            if(StringUtils.isEmpty(msg)){

                //1506
                throw new NotFoundMsgException("해당 코드에 존재하는 메시지가 없음");
            }

            SmsAgentRequestDto smsAgentRequestDto = SmsAgentRequestDto.builder()
                    .smsCd(sendSMSCodeRequestDto.getSmsCd())
                    .smsId(sendSMSCodeRequestDto.getCtn())
                    .replacement(sendSMSCodeRequestDto.getReplacement())
                    .smsMsg(msg)
                    .build();

            smsGatewayResponseDto = sendSmsCode(smsAgentRequestDto, false);

//        } catch (CustomExceptionHandler e) {
//            log.info("[smsCode] {} {} {} {}", sendSMSCodeRequestDto.toString(), e.getClass().getSimpleName(), e.getCause(), e.getMessage());
//            throw e;
        } catch (Exception e) {
//            cLog.errorLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, e.getClass().getSimpleName(), e.getMessage());
//            throw new CustomExceptionHandler(e);

            log.info("[smsCode] {} {} {} {}", sendSMSCodeRequestDto.toString(), e.getClass().getSimpleName(), e.getCause(), e.getMessage());

            //9999
            throw new RuntimeException("기타 오류");

        }

        //#########[LOG END]#########
//        cLog.endLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, resultVO.getFlag());
//        return resultVO;

        return smsGatewayResponseDto;
    }

    public SmsGatewayResponseDto sendSmsCode(SmsAgentRequestDto smsAgentRequestDto, boolean encryptYn) {

        return retryModuleDomainService.smsSendCode(smsAgentRequestDto, encryptYn);

    }


    public String selectSmsMsg(String sms_cd) {

        Map<String, String> map = null;
        try{
            map = callSettingApi(sms_cd);
        }
        catch(java.lang.Exception e){

        }

        if(null == map || map.size() == 0){
            log.debug("[selectSmsMsg] Cannot found URL");
            return "";
        }

        return map.get("sms_cd");
    }


    @Cacheable(value="smsMessageCacheVaue", key="smsMessageCache")
    public Map<String,String> callSettingApi(String sms_cd) {

        Map<String, String> map = new HashMap<String, String>();

        try {

            String url = smsSettingRestUrl + smsSettingRestPath;
            String method = StringUtils.defaultIfEmpty(smsSettingRequestMethod, "GET");
            int timeout = Integer.parseInt(StringUtils.defaultIfEmpty(smsSettingTimeout, "5000"));
            if(StringUtils.isEmpty(url)){
                log.debug("[selectSmsMsg]Cannot found URL");
                return null;
            }

            //============ Start [setting API 호출 캐시등록] =============

            //setting API 호출관련 파라메타 셋팅
            CallSettingRequestDto prm = CallSettingRequestDto.builder().build();//callSettingApi파라메타
            prm.setSaId(smsSettingRestSaId);//ex) MMS:mms SMS:sms
            prm.setStbMac(smsSettingRestStbMac);//ex) MMS:mms SMS:sms
            prm.setCodeId(sms_cd);//ex) M011
            prm.setSvcType(smsSettingRestSvcType);//ex) MMS:E SMS:I

            //setting API 호출하여 메세지 등록
            CallSettingResultMapDto callSettingApi = apiClient.callSettingApi(prm);

            // Send a message
            String logStr = "\n [Start] ############## callSettingApi로 FeignClient 메세지목록 호출 ############## \n";
            logStr += "\n [ 출발지 : SmsAgentDomainService.sendMmsCode ] \n";
            logStr += "\n [ 도착지 : CallSettingDomainFeignClient.callSettingApi ] \n";
            logStr += "\n [ 요청주소 : "+smsSettingRestUrl+smsSettingRestPath+" ] \n";
            logStr += "\n [ 매개변수 : " + prm.toString() + " ] \n";
            logStr += "\n ------------- Start 매개변수 가이드 -------------\n";
            logStr += "\n * sa_id:가입자정보 \n";
            logStr += "\n * stb_mac:가입자 STB MAC Address \n";
            logStr += "\n * ctn:발송대상 번호 \n";
            logStr += "\n * replacement:치환문자 \n";
            logStr += "\n * mms_cd:MMS 메시지 코드 \n";
            logStr += "\n   - M001 : 모바일tv 앱 설치안내 문자 \n";
            logStr += "\n   - M002 : 프로야구 앱 설치안내 문자 \n";
            logStr += "\n   - M003 : 아이들나라 앱 설치안내 문자 \n";
            logStr += "\n   - M004 : 골프 앱 설치안내 문자 \n";
            logStr += "\n   - M005 : 아이돌Live 앱 설치안내 문자 \n";
            logStr += "\n ------------- End 매개변수 가이드 -------------\n\n";
            logStr += "\n [ 리턴결과 : " + callSettingApi.toString() + " ] \n";
            logStr += "\n [End] ##############  ############## callSettingApi로 FeignClient 메세지목록 호출 ############## \n\n";
            log.debug(logStr);
            //메세지목록 조회결과 취득
            List<CallSettingDto> settingApiList =  callSettingApi.getResult().getRecordset();

            //============ End [setting API 호출 캐시등록] =============

            if(callSettingApi.getResult().getTotalCount() > 0) {

                log.debug("sms_cd(메시지내용) {} " , settingApiList.get(0).getCodeName());
                map.put("sms_cd", settingApiList.get(0).getCodeName());
            }

        } catch (Exception e) {
            log.debug("[callSettingApi][Call]["+e.getClass().getName()+"]"+e.getMessage());
            //9999
            throw new RuntimeException("기타 오류");
        }
        return map;
    }

}

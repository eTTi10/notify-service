package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.request.inner.SmsAgentRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;


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

    /**
     * 문자발송(문자내용으로발송)
     * @param sendSmsRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) {

        log.debug("[sendSms] - [{}]]", sendSmsRequestDto.toString());

        String s_ctn = sendSmsRequestDto.getSCtn();
        String r_ctn = sendSmsRequestDto.getRCtn();
        String msg = sendSmsRequestDto.getMsg();

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

            return SmsProviderDomainService.send(s_ctn, r_ctn, msg);

        } catch (SocketException ex) {

            log.info("[SmsAgentDomainService][SocketException]"+ ex.getCause() + ":" + ex.getMessage());
            throw new SocketException();

        } catch (SocketTimeOutException ex) {

            log.info("[SmsAgentDomainService][SocketTimeOutException]"+ ex.getCause() + ":" + ex.getMessage());
            throw new SocketTimeOutException();

        } catch (Exception e) {

            log.info("[SmsAgentDomainService][Ex]"+ e.getClass().getName() + ":" + e.getMessage());
            throw new RuntimeException("기타오류");
        }


    }

    /**
     * 문자발송(코드로발송)
     * @param sendSMSCodeRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSmsCode(SendSmsCodeRequestDto sendSMSCodeRequestDto) {

        //#########[LOG SET]#########
        log.debug ("[smsCode] - {}", sendSMSCodeRequestDto.toString());
        String sms_cd = sendSMSCodeRequestDto.getSmsCd();

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

            return retryModuleDomainService.smsSendCode(smsAgentRequestDto, false);

        } catch (Exception e) {

            log.info("[smsCode Exception] {} {} {} {}", e.getClass().getSimpleName(), e.getCause(), e.getMessage());

            throw new RuntimeException("기타 오류");

        }

        //#########[LOG END]#########
//        cLog.endLog("[smsCode]",sa_id, stb_mac, sms_cd, replacement, resultVO.getFlag());

    }

    /**
     * 문자내용 가져온다
      * @param sms_cd
     * @return String 문자내용
     */
    private String selectSmsMsg(String sms_cd) {

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


    /**
     * API호출하여 리스트 중 원하는 문자내용만 리턴
     * @param sms_cd
     * @return Map<sms_cd, 문자내용>
     */
//    @Cacheable(value="smsMessageCacheValue", key="smsMessageCache")
    private Map<String,String> callSettingApi(String sms_cd) {

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
            CallSettingResultMapDto callSettingApi = apiClient.smsCallSettingApi(prm);

            //메세지목록 조회결과 취득
            List<CallSettingDto> settingApiList =  callSettingApi.getResult().getRecordset();

            //============ End [setting API 호CallSettingResultMapDto출 캐시등록] =============

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

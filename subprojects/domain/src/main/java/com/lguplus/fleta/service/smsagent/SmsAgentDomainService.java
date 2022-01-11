package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.client.SmsAgentDomainClient;
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

    @Value("${sms.send.retry}")
    private String smsRetry;

    @Value("${sms.send.busy.retry}")
    private String smsBusyRetry;

    @Value("${sms.sender.retry.sleep.ms}")
    private String smsSleepTime;

    @Value("${sms.sender.no}")
    private String smsSenderNo;

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

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemErrorException}")
    private String codeSystemErrorException;

    @Value("${error.message.1500}")
    private String messageSystemError;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SocketException}")
    private String codeSocketException;

    @Value("${error.message.5101}")
    private String messageSocketException;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SocketTimeOutException}")
    private String codeSocketTimeOutException;

    @Value("${error.message.5102}")
    private String messageSocketTimeOutException;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemBusyException}")
    private String codeSystemBusyException;

    @Value("${error.flag.java.lang.Throwable}")
    private String codeRunTimeException;

    @Value("${error.message.9999}")
    private String messageRunTimeException;

    private final CallSettingDomainClient apiClient;
    private final SmsAgentDomainClient smsAgentClient;

    private final static String sep = "\\|";

    private int callCount = 0;
    private int systemEr = 0;
    private int busyEr = 0;

    private int retry;
    private int busyRetry;
    private int sleepTime;

    /**
     * 문자발송(문자내용으로발송)
     * @param sendSmsRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) {

        log.debug("[sendSms] - [{}]]", sendSmsRequestDto.toString());

        String sCtn = sendSmsRequestDto.getSCtn();
        String rCtn = sendSmsRequestDto.getRCtn();
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

            return smsAgentClient.send(sCtn, rCtn, msg);

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

        String smsCd = sendSMSCodeRequestDto.getSmsCd();
        String msg = Optional.of(selectSmsMsg(smsCd)).orElseThrow(()-> new NotFoundMsgException("해당 코드에 존재하는 메시지가 없음")); //1506

        SmsAgentRequestDto smsAgentRequestDto = SmsAgentRequestDto.builder()
                .smsCd(sendSMSCodeRequestDto.getSmsCd())
                .smsId(sendSMSCodeRequestDto.getCtn())
                .replacement(sendSMSCodeRequestDto.getReplacement())
                .smsMsg(msg)
                .build();

        return retrySmsSend(smsAgentRequestDto);

    }

    /**
     * 문자발송(코드로발송)에서 재시도가 가능하게 하는 재귀함수
     * @param smsAgentRequestDto
     * @return
     */
    public SmsGatewayResponseDto retrySmsSend(SmsAgentRequestDto smsAgentRequestDto) {

        retry = Integer.parseInt(StringUtils.defaultIfEmpty(smsRetry, "0"));
        busyRetry = Integer.parseInt(StringUtils.defaultIfEmpty(smsBusyRetry, "5"));
        sleepTime = Integer.parseInt(StringUtils.defaultIfEmpty(smsSleepTime, "1000"));

        //0:재처리 안함 1:SMS서버 에러로 재처리 2:서버가 busy하여 재처리
        int checkRetry = 0;
        String sendMsg = "";

        SmsGatewayResponseDto smsGatewayResponseDto;

        try {
            callCount++;
            sendMsg = convertMsg(smsAgentRequestDto.getSmsMsg(), smsAgentRequestDto.getReplacement());

            smsGatewayResponseDto = smsAgentClient.send(smsSenderNo, smsAgentRequestDto.getSmsId(), sendMsg);

        } catch (Exception e) {

            log.info("[retrySmsSend][Exception] name : " + e.getClass().getName() + ",  " + codeRunTimeException + " : " + e.getMessage() + " , cause : " + e.getCause());
            //9999
            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(codeRunTimeException)
                    .message(messageRunTimeException)
                    .build();
        }

        //retry여부를 판단한다.
        if( smsGatewayResponseDto.getFlag().equals(codeSystemErrorException) || smsGatewayResponseDto.getFlag().equals(codeRunTimeException) ){
            // 시스템 장애이거나 기타오류 일 경우
            checkRetry = 1;
            systemEr++;

        }else if( smsGatewayResponseDto.getFlag().equals(codeSystemBusyException) ){
            // 메시지 처리 수용 한계 초과일 경우
            checkRetry = 2;
            busyEr++;
        }

        log.debug("[smsSend]["+smsAgentRequestDto.getPtDay()+"]["+smsAgentRequestDto.getSmsCd()+"]["+smsAgentRequestDto.getSmsId()+"]["+sendMsg+"][callCount:"+callCount+"][systemEr:"+systemEr+"] [retry:"+retry+"] [busyEr:"+busyEr+"] [busyRetry:"+busyRetry+"] ["+smsGatewayResponseDto.getFlag()+"]["+smsGatewayResponseDto.getMessage()+"]");
        //재시도에 해당되지 않는 경우 || 재시도설정횟수보다 재시도한 횟수가 클 경우 || 메시지 처리 수용한계 설정횟수보다 처리 횟수가 클 경우
        if(checkRetry == 0 || systemEr > retry || busyEr > busyRetry){

            return smsGatewayResponseDto;
        }else{
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {}


            return retrySmsSend(smsAgentRequestDto);
        }

    }


    /**
     * API호출하여 리스트 중 원하는 문자내용만 리턴
     * @param smsCd
     * @return Map<sms_cd, 문자내용>
     */
    private Map<String,String> callSettingApi(String smsCd) {

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
            CallSettingRequestDto prm = CallSettingRequestDto.builder()
                    .saId(smsSettingRestSaId)
                    .stbMac(smsSettingRestStbMac)
                    .codeId(smsCd)
                    .svcType(smsSettingRestSvcType)
                    .build();

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


    /**
     * 문자내용 가져온다
     * @param smsCd
     * @return String 문자내용
     */
    private String selectSmsMsg(String smsCd) {

        Map<String, String> map = null;
        try{
            map = callSettingApi(smsCd);
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
     * 지정된 문자열로 변경하여 리턴한다.
     * @param msg
     * @param replacement
     * @return
     */
    private static String convertMsg(String msg, String replacement){

        if(StringUtils.isEmpty(replacement)) {
            return msg;
        }
        else{
            String[] rep = replacement.split(sep);
            int i = 1;
            for(String t : rep){
                String repTxt = "{" + i + "}";
                msg = msg.replace(repTxt, t);
                i++;
            }
            return msg;
        }
    }


}

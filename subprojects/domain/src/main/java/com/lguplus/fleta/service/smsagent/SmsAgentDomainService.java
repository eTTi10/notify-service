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

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;


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

    @Value("${sms.setting.rest_sa_id}")
    private String smsSettingRestSaId;

    @Value("${sms.setting.rest_stb_mac}")
    private String smsSettingRestStbMac;

    @Value("${sms.setting.rest_svc_type}")
    private String smsSettingRestSvcType;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.PhoneNumberErrorException}")
    private String codePhoneNumberErrorException;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.MsgTypeErrorException}")
    private String codeMsgTypeErrorException;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemBusyException}")
    private String codeSystemBusyException;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemErrorException}")
    private String codeSystemErrorException;

    @Value("${error.smsagent.etc.flag}")
    private String codeEtcException;

    @Value("${error.smsagent.etc.message}")
    private String messageEtcException;

    private final CallSettingDomainClient apiClient;
    private final SmsAgentDomainClient smsAgentClient;

    private static final String SEP = "\\|";

    private int callCount = 0;
    private int systemEr = 0;
    private int busyEr = 0;

    /**
     * 문자발송(문자내용으로발송)
     * @param sendSmsRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        String sCtn = sendSmsRequestDto.getSCtn();
        String rCtn = sendSmsRequestDto.getRCtn();
        String message = sendSmsRequestDto.getMsg();

        if (StringUtils.isEmpty(agentNoSendUse)) {

            throw new SmsAgentEtcException(messageEtcException);
        }

        if ("1".equals(agentNoSendUse)) {

            if (StringUtils.isEmpty(agentNoSendTime)) {

                throw new ServerSettingInfoException("서버 설정 정보 오류");
            }
            else {

                Calendar cal = Calendar.getInstance();
                Calendar startCal = Calendar.getInstance();
                Calendar endCal = Calendar.getInstance();

                String[] noSendAry = agentNoSendTime.split("\\|");

                log.debug("------------------------------------------");

                int startTime = Integer.parseInt(noSendAry[0]);
                int endTime = Integer.parseInt(noSendAry[1]);
                log.debug("startTime: {} endTime: {}", startTime, endTime);

                startCal.set(Calendar.HOUR_OF_DAY, startTime);
                startCal.set(Calendar.MINUTE, 0);
                startCal.set(Calendar.SECOND, 0);
                startCal.set(Calendar.MILLISECOND, 0);

                if (startTime >= endTime) {
                    endCal.add(Calendar.DAY_OF_MONTH, 1);
                    log.debug("startTime >= endTime YES");
                }

                endCal.set(Calendar.HOUR_OF_DAY, endTime);
                endCal.set(Calendar.MINUTE, 0);
                endCal.set(Calendar.SECOND, 0);
                endCal.set(Calendar.MILLISECOND, 0);

                if (cal.after(startCal) && cal.before(endCal)) {

                    throw new NotSendTimeException("전송 가능한 시간이 아님");
                }

            }
        }

        return smsAgentClient.send(sCtn, rCtn, message);

    }

    /**
     * 문자발송(코드로발송)
     * @param sendSMSCodeRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSmsCode(SendSmsCodeRequestDto sendSMSCodeRequestDto) {

        //#########[LOG SET]#########
        log.debug ("[smsCode] sendSMSCodeRequestDto - {}", sendSMSCodeRequestDto.toString());

        String smsCd = sendSMSCodeRequestDto.getSmsCd();
        String message = Optional.of(callSettingApi(smsCd)).orElse("");

        if (message.equals("")) {
            throw new NotFoundMsgException("해당 코드에 존재하는 메시지가 없음"); //1506
        }

        SmsAgentRequestDto smsAgentRequestDto = SmsAgentRequestDto.builder()
                .smsCd(sendSMSCodeRequestDto.getSmsCd())
                .smsId(sendSMSCodeRequestDto.getCtn())
                .replacement(sendSMSCodeRequestDto.getReplacement())
                .smsMsg(message)
                .build();

        return retrySmsSend(smsAgentRequestDto);

    }

    /**
     * 문자발송(코드로발송)에서 재시도가 가능하게 하는 재귀함수
     * @param smsAgentRequestDto
     * @return
     */
    public SmsGatewayResponseDto retrySmsSend(SmsAgentRequestDto smsAgentRequestDto) {

        int retry = Integer.parseInt(StringUtils.defaultIfEmpty(smsRetry, "0"));
        int busyRetry = Integer.parseInt(StringUtils.defaultIfEmpty(smsBusyRetry, "5"));
        int sleepTime = Integer.parseInt(StringUtils.defaultIfEmpty(smsSleepTime, "1000"));

        //0:재처리 안함 1:SMS서버 에러로 재처리 2:서버가 busy하여 재처리
        int checkRetry = 0;
        String sendMessage = "";

        SmsGatewayResponseDto smsGatewayResponseDto;

        try {
            callCount++;
            sendMessage = convertMsg(smsAgentRequestDto.getSmsMsg(), smsAgentRequestDto.getReplacement());

            smsGatewayResponseDto = smsAgentClient.send(smsSenderNo, smsAgentRequestDto.getSmsId(), sendMessage);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(codeEtcException)
                    .message(messageEtcException)
                    .build();

        } catch (PhoneNumberErrorException e) {

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(codePhoneNumberErrorException)
                    .message(e.getMessage())
                    .build();

        } catch (MsgTypeErrorException e) {

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(codeMsgTypeErrorException)
                    .message(e.getMessage())
                    .build();

        } catch (SystemBusyException e) {

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(codeSystemBusyException)
                    .message(e.getMessage())
                    .build();

        } catch (SystemErrorException e) {

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(codeSystemErrorException)
                    .message(e.getMessage())
                    .build();

        } catch (SmsAgentEtcException e) {

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(messageEtcException)
                    .message(e.getMessage())
                    .build();

        } catch (Exception e) {

            log.info("[retrySmsSend][Exception] name : " + e.getClass().getName() + ",  " + " : " + e.getMessage() + " , cause : " + e.getCause());
            //9999
            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(codeEtcException)
                    .message(e.getMessage())
                    .build();
        }

        //retry여부를 판단한다.
        if( smsGatewayResponseDto.getFlag().equals(codeSystemErrorException) || smsGatewayResponseDto.getFlag().equals(codeEtcException) ){
            // 시스템 장애이거나 기타오류 일 경우
            checkRetry = 1;
            systemEr++;

        }else if( smsGatewayResponseDto.getFlag().equals(codeSystemBusyException) ){
            // 메시지 처리 수용 한계 초과일 경우
            checkRetry = 2;
            busyEr++;
        }

        log.debug("[retrySmsSend]["+smsAgentRequestDto.getPtDay()+"]["+smsAgentRequestDto.getSmsCd()+"]["+smsAgentRequestDto.getSmsId()+"]["+sendMessage+"][callCount:"+callCount+"][systemEr:"+systemEr+"] [retry:"+retry+"] [busyEr:"+busyEr+"] [busyRetry:"+busyRetry+"] ["+smsGatewayResponseDto.getFlag()+"]["+smsGatewayResponseDto.getMessage()+"]");

        if(checkRetry == 0 || systemEr > retry || busyEr > busyRetry){
            //재시도에 해당되지 않는 경우 or 재시도설정횟수보다 재시도한 횟수가 클 경우 or 메시지 처리 수용한계 설정횟수보다 처리 횟수가 클 경우
            return smsGatewayResponseDto;
        }else{
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                throw new SmsAgentEtcException(messageEtcException);
            }

            return retrySmsSend(smsAgentRequestDto);
        }

    }


    /**
     * API호출하여 리스트 중 원하는 문자내용만 리턴
     * @param smsCd
     * @return Map<sms_cd, 문자내용>
     */
    private String callSettingApi(String smsCd) {

        try {

            //============ Start [setting API 호출 캐시등록] =============

            //setting API 호출관련 파라메타 셋팅 <-- sos6871수정
            CallSettingRequestDto prm = CallSettingRequestDto.builder()
                    //.saId(smsSettingRestSaId) <-- sos6871수정
                    //.stbMac(smsSettingRestStbMac) <-- sos6871수정
                    .code(smsCd) //<-- sos6871수정
                    .svcType(smsSettingRestSvcType)
                    .build();

            //setting API 호출하여 메세지 등록 <-- sos6871수정
            CallSettingResultMapDto callSettingApi = apiClient.smsCallSettingApi(prm);

            //메세지목록 조회결과 취득 <-- sos6871수정
            CallSettingDto settingApi =  callSettingApi.getResult().getData();

            //============ End [setting API 호CallSettingResultMapDto출 캐시등록] =============

            if(settingApi != null) { //<-- sos6871수정

                log.debug("sms_cd(메시지내용) {} " , settingApi.getName()); //<-- sos6871
                return settingApi.getName(); //<-- sos6871수정
            }
            else {
                return "";
            }

        } catch (Exception e) {
            log.debug("[callSettingApi][Call]["+e.getClass().getName()+"]"+e.getMessage());
            //9999
            throw new SmsAgentEtcException(messageEtcException);
        }
    }


    /**
     * 지정된 문자열로 변경하여 리턴한다.
     * @param msg
     * @param replacement
     * @return
     */
    private static String convertMsg(String msg, String replacement){

        if (!StringUtils.isEmpty(replacement)) {
            String[] rep = replacement.split(SEP);
            int i = 1;
            for (String t : rep) {
                String repTxt = "{" + i + "}";
                msg = msg.replace(repTxt, t);
                i++;
            }
        }
        return msg;
    }


}

package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.SmsAgentClient;
import com.lguplus.fleta.client.SettingDomainClient;
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

import javax.annotation.PostConstruct;
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
    private boolean agentNoSendUse;

    @Value("${agent.no.sendtime.from}")
    private int agentNoSendTimeFrom;

    @Value("${agent.no.sendtime.to}")
    private int agentNoSendTimeTo;

    @Value("${sms.send.retry}")
    private int smsRetry;

    @Value("${sms.send.busy.retry}")
    private int smsBusyRetry;

    @Value("${sms.sender.retry.sleep.ms}")
    private long smsSleepTime;

    @Value("${sms.sender.no}")
    private String smsSenderNo;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemBusyException}")
    private String codeSystemBusyException;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemErrorException}")
    private String codeSystemErrorException;

    @Value("${error.smsagent.etc.flag}")
    private String codeEtcException;

    @Value("${error.smsagent.etc.message}")
    private String messageEtcException;

    private final SettingDomainClient apiClient;
    private final SmsAgentClient smsAgentClient;

    private static final String SEP = "\\|";

    private int callCount = 0;
    private int systemEr = 0;
    private int busyEr = 0;
    private int retry;
    private int busyRetry;
    private long sleepTime;

    /**
     * @Value로 가져온 프로퍼티 초기화를 위해
     */
    @PostConstruct
    public void init() {

        retry = smsRetry;
        busyRetry = smsBusyRetry;
        sleepTime = smsSleepTime;
    }

    /**
     * 문자발송(문자내용으로발송)
     * @param sendSmsRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        String sCtn = sendSmsRequestDto.getSCtn();
        String rCtn = sendSmsRequestDto.getRCtn();
        String message = sendSmsRequestDto.getMsg();

        if (agentNoSendUse) {

            Calendar cal = Calendar.getInstance();
            Calendar startCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();

            log.debug("------------------------------------------");

            startCal.set(Calendar.HOUR_OF_DAY, agentNoSendTimeFrom);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);

            if (agentNoSendTimeFrom >= agentNoSendTimeTo) {
                endCal.add(Calendar.DAY_OF_MONTH, 1);
            }

            endCal.set(Calendar.HOUR_OF_DAY, agentNoSendTimeTo);
            endCal.set(Calendar.MINUTE, 0);
            endCal.set(Calendar.SECOND, 0);
            endCal.set(Calendar.MILLISECOND, 0);

            if (cal.after(startCal) && cal.before(endCal)) {

                //1504
                throw new SmsAgentCustomException("1504", "전송 가능한 시간이 아님");
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

        callCount = 0;
        systemEr = 0;
        busyEr = 0;

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

        //0:재처리 안함 1:SMS서버 에러로 재처리 2:서버가 busy하여 재처리
        CheckRetryType checkRetry = CheckRetryType.NO_RETRY;
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

        } catch (SmsAgentCustomException e) {

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag(e.getCode())
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
            checkRetry = CheckRetryType.RETRY_CAUSE_ERROR;
            systemEr++;

        }else if( smsGatewayResponseDto.getFlag().equals(codeSystemBusyException) ){
            // 메시지 처리 수용 한계 초과일 경우
            checkRetry = CheckRetryType.RETRY_CAUSE_BUSY;
            busyEr++;
        }

        log.debug("[retrySmsSend]["+smsAgentRequestDto.getPtDay()+"]["+smsAgentRequestDto.getSmsCd()+"]["+smsAgentRequestDto.getSmsId()+"]["+sendMessage+"][callCount:"+callCount+"][systemEr:"+systemEr+"] [retry:"+retry+"] [busyEr:"+busyEr+"] [busyRetry:"+busyRetry+"] ["+smsGatewayResponseDto.getFlag()+"]["+smsGatewayResponseDto.getMessage()+"]");

        if(checkRetry == CheckRetryType.NO_RETRY || systemEr > retry || busyEr > busyRetry){
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

            //setting API 호출관련 파라메타 셋팅
            CallSettingRequestDto prm = CallSettingRequestDto.builder()
                    .code(smsCd)
                    .svcType("I")
                    .build();

            //setting API 호출하여 메세지 등록
            CallSettingResultMapDto callSettingApi = apiClient.callSettingApi(prm);

            //메세지목록 조회결과 취득
            CallSettingDto settingApi =  callSettingApi.getResult().getData();

            //============ End [setting API 호CallSettingResultMapDto출 캐시등록] =============

            if(callSettingApi.getResult().getDataCount() > 0) {

                log.debug("sms_cd(메시지내용) {} " , settingApi.getName());
                return settingApi.getName();
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

    enum CheckRetryType {

        NO_RETRY,
        RETRY_CAUSE_ERROR,
        RETRY_CAUSE_BUSY
    }
}

package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.inner.SmsAgentRequestDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.smsagent.SocketTimeOutException;
import com.lguplus.fleta.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.net.SocketTimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryModuleDomainService {

    private final static String sep = "\\|";

    private int callCount = 0;
    private int systemEr = 0;
    private int busyEr = 0;

    @Value("${sms.send.retry}")
    private String smsRetry;

    @Value("${sms.send.busy.retry}")
    private String smsBusyRetry;

    @Value("${sms.sender.retry.sleep.ms}")
    private String smsSleepTime;

    @Value("${sms.sender.no}")
    private String smsSenderNo;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SocketException}")
    private String codeSocketException;

    @Value("${error.message.5101}")
    private String messageSocketException;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SocketTimeOutException}")
    private String codeSocketTimeOutException;

    @Value("${error.message.5102}")
    private String messageSocketTimeOutException;

    @Value("${error.flag.java.lang.Throwable}")
    private String codeRunTimeException;

    @Value("${error.message.9999}")
    private String messageRunTimeException;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemErrorException}")
    private String codeSystemErrorException;

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemBusyException}")
    private String codeSystemBusyException;

    private final SmsProviderDomainService smsProviderDomainService;

    private int retry = Integer.parseInt(StringUtils.defaultIfEmpty(smsRetry, "0"));
    private int busyRetry = Integer.parseInt(StringUtils.defaultIfEmpty(smsBusyRetry, "5"));
    private int sleepTime = Integer.parseInt(StringUtils.defaultIfEmpty(smsSleepTime, "1000"));

    public SmsGatewayResponseDto smsSendCode(SmsAgentRequestDto smsAgentRequestDto, boolean encryptYn) {

        //0:재처리 안함 1:SMS서버 에러로 재처리 2:서버가 busy하여 재처리
        int checkRetry = 0;
        String sendMsg;
        String responseCode = "";
        String responseMessage = "";


        try {
            callCount++;
            sendMsg = convertMsg(smsAgentRequestDto.getSmsMsg(), smsAgentRequestDto.getReplacement());

            return smsProviderDomainService.send(smsSenderNo
                    , encryptYn ? AesUtil.decryptAES(smsAgentRequestDto.getSmsId()) : smsAgentRequestDto.getSmsId(), sendMsg);

//			###############TEST
//            responseCode = "0000";
//            responseMessage = "성공";
//            ###############TEST

        } catch (SocketException ex) {
            log.info("[smsSend][SocketException]"+ ex.getCause() + ":" + ex.getMessage());

            responseCode = codeSocketException;
            responseMessage = messageSocketException;

        } catch (SocketTimeoutException ex) {

            log.info("[smsSend][SocketTimeoutException]"+ ex.getCause() + ":" + ex.getMessage());

            responseCode = codeSocketTimeOutException;
            responseMessage = messageSocketTimeOutException;

        } catch (Exception e) {

            log.info("[smsSend][Exception]"+ e.getClass().getName() + ":" + e.getMessage());
            //9999
            responseCode = codeRunTimeException;
            responseMessage = messageRunTimeException;

        }

        //retry여부를 판단한다.
        if( responseCode.equals(codeSystemErrorException) || responseCode.equals(codeRunTimeException) ){
            // 시스템 장애이거나 기타오류 일 경우
            checkRetry = 1;
            systemEr++;

        }else if( responseCode.equals(codeSystemBusyException) ){
            // 메시지 처리 수용 한계 초과일 경우
            checkRetry = 2;
            busyEr++;
        }

//        log.debug("[smsSend]["+smsAgentRequestDto.getPtDay()+"]["+smsAgentRequestDto.getSmsCd()+"]["+smsAgentRequestDto.getSmsId()+"]["+sendMsg+"][callCount:"+callCount+"][systemEr:"+systemEr+"][busyEr:"+busyEr+"]["+smsGatewayResponseDto.getFlag()+"]["+smsGatewayResponseDto.getMessage()+"]");
        //재도시에 해당되지 않는 경우 || 재시도설정횟수보다 재시도한 횟수가 클 경우 || 메시지 처리 수용한계 설정횟수보다 처리 횟수가 클 경우
        if(checkRetry == 0 || systemEr > retry || busyEr > busyRetry){

            return SmsGatewayResponseDto.builder()
                    .flag(responseCode)
                    .message(responseMessage)
                    .build();
        }else{
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {}


            return smsSendCode(smsAgentRequestDto, encryptYn);
        }


    }


    private void clear(){
        callCount = 0;
        systemEr = 0;
        busyEr = 0;
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

package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.inner.SmsAgentRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.push.SocketException;
import com.lguplus.fleta.exception.smsagent.SocketTimeOutException;
import com.lguplus.fleta.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    private final SmsProviderDomainService smsProviderDomainService;

    private int retry = Integer.parseInt(StringUtils.defaultIfEmpty(smsRetry, "0"));
    private int busyRetry = Integer.parseInt(StringUtils.defaultIfEmpty(smsBusyRetry, "5"));
    private int sleepTime = Integer.parseInt(StringUtils.defaultIfEmpty(smsSleepTime, "1000"));

    public SmsGatewayResponseDto smsSendCode(SmsAgentRequestDto smsAgentRequestDto, boolean encryptYn) {

        //0:재처리 안함 1:SMS서버 에러로 재처리 2:서버가 busy하여 재처리
        int checkRetry = 0;
        String sendMsg;

        SmsGatewayResponseDto smsGatewayResponseDto = new SmsGatewayResponseDto();

        try {
            callCount++;
            sendMsg = convertMsg(smsAgentRequestDto.getSmsMsg(), smsAgentRequestDto.getReplacement());

            smsGatewayResponseDto = smsProviderDomainService.send(smsSenderNo
                    , encryptYn ? AesUtil.decryptAES(smsAgentRequestDto.getSmsId()) : smsAgentRequestDto.getSmsId(), sendMsg);

			//###############TEST
            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag("200")
                    .message("성공")
                    .build();
            //###############TEST

        } catch (SocketException ex) {

            log.info("[smsSend][SocketException]"+ ex.getCause() + ":" + ex.getMessage());

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag("200")
                    .message("소켓 에러")
                    .build();

        } catch (SocketTimeoutException ex) {

            log.info("[smsSend][SocketException]"+ ex.getCause() + ":" + ex.getMessage());

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag("200")
                    .message("소켓 에러")
                    .build();

        } catch (Exception e) {

            log.info("[smsSend][Ex]"+ e.getClass().getName() + ":" + e.getMessage());
            //9999

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                    .flag("200")
                    .message("기타 오류")
                    .build();

        }

        //retry여부를 판단한다.
        if("1500".equals(smsGatewayResponseDto.getFlag()) || "9999".equals(smsGatewayResponseDto.getFlag())){
            // 시스템 장애이거나 기타오류 일 경우
            checkRetry = 1;
            systemEr++;
        }else if("1503".equals(smsGatewayResponseDto.getFlag())){
            // 메시지 처리 수용 한계 초과일 경우
            checkRetry = 2;
            busyEr++;
        }

//        log.debug("[smsSend]["+smsAgentRequestDto.getPtDay()+"]["+smsAgentRequestDto.getSmsCd()+"]["+smsAgentRequestDto.getSmsId()+"]["+sendMsg+"][callCount:"+callCount+"][systemEr:"+systemEr+"][busyEr:"+busyEr+"]["+smsGatewayResponseDto.getFlag()+"]["+smsGatewayResponseDto.getMessage()+"]");

        if(checkRetry == 0 || systemEr > retry || busyEr > busyRetry){
            return smsGatewayResponseDto;
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

        if(StringUtils.isEmpty(replacement)) return msg;
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

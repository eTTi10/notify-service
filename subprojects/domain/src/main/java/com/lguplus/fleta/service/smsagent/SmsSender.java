package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.SmsAgentClient;
import com.lguplus.fleta.data.dto.request.inner.SmsAgentRequestDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.SmsAgentCustomException;
import com.lguplus.fleta.exception.smsagent.SmsAgentEtcException;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


/**
 * SMSAgentDomainService 설명
 * <p>
 * 최초작성 2021-11-23
 **/
@Slf4j
public class SmsSender {

    private static final String SEP = "\\|";
    private final SmsAgentClient smsAgentClient;

    private final int smsRetry;

    private final int smsBusyRetry;

    private final long smsSleepTime;

    private final String smsSenderNo;

    private final String codeSystemBusyException;

    private final String codeSystemErrorException;

    private final String codeEtcException;

    private final String messageEtcException;

    private int callCount = 0;
    private int systemEr = 0;
    private int busyEr = 0;
    private int retry;
    private int busyRetry;
    private long sleepTime;

    public SmsSender(SmsAgentClient smsAgentClient, SmsAgentDomainService smsAgentDomainService) {
        this.smsAgentClient = smsAgentClient;

        this.smsRetry = smsAgentDomainService.getSmsRetry();

        this.smsBusyRetry = smsAgentDomainService.getSmsBusyRetry();

        this.smsSleepTime = smsAgentDomainService.getSmsSleepTime();

        this.smsSenderNo = smsAgentDomainService.getSmsSenderNo();

        this.codeSystemBusyException = smsAgentDomainService.getCodeSystemBusyException();

        this.codeSystemErrorException = smsAgentDomainService.getCodeSystemErrorException();

        this.codeEtcException = smsAgentDomainService.getCodeEtcException();

        this.messageEtcException = smsAgentDomainService.getMessageEtcException();

        retry = smsRetry;
        busyRetry = smsBusyRetry;
        sleepTime = smsSleepTime;
    }

    /**
     * 지정된 문자열로 변경하여 리턴한다.
     *
     * @param msg
     * @param replacement
     * @return
     */
    private static String convertMsg(String msg, String replacement) {

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

    /**
     * @Value로 가져온 프로퍼티 초기화를 위해
     */

    /**
     * 문자발송(코드로발송)에서 재시도가 가능하게 하는 재귀함수
     *
     * @param smsAgentRequestDto
     * @return
     */
    public SmsGatewayResponseDto sendSms(SmsAgentRequestDto smsAgentRequestDto) {

        //0:재처리 안함 1:SMS서버 에러로 재처리 2:서버가 busy하여 재처리
        CheckRetryType checkRetry = CheckRetryType.NO_RETRY;

        SmsGatewayResponseDto smsGatewayResponseDto = convertAndSendSms(smsAgentRequestDto);

        //retry여부를 판단한다.
        checkRetry = getCheckRetryType(checkRetry, smsGatewayResponseDto);

        log.debug("[retrySmsSend][" + smsAgentRequestDto.getPtDay() + "][" + smsAgentRequestDto.getSmsCd() + "][" + smsAgentRequestDto.getSmsId() + "][callCount:" + callCount + "][systemEr:" + systemEr + "] [retry:" + retry + "] [busyEr:" + busyEr + "] [busyRetry:" + busyRetry + "] [" + smsGatewayResponseDto.getFlag() + "][" + smsGatewayResponseDto.getMessage() + "]");

        return retryIfChecked(smsAgentRequestDto, checkRetry, smsGatewayResponseDto);

    }

    private SmsGatewayResponseDto convertAndSendSms(SmsAgentRequestDto smsAgentRequestDto) {
        SmsGatewayResponseDto smsGatewayResponseDto;

        try {
            callCount++;
            String sendMessage = convertMsg(smsAgentRequestDto.getSmsMsg(), smsAgentRequestDto.getReplacement());
            log.debug("sendMessage {}", sendMessage);
            smsGatewayResponseDto = smsAgentClient.send(smsSenderNo, smsAgentRequestDto.getSmsId(), sendMessage);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                .flag(codeEtcException)
                .message(messageEtcException)
                .build();

        } catch (SmsAgentCustomException e) {
            log.error(e.getMessage() , e);
            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                .flag(e.getCode())
                .message(e.getMessage())
                .build();

        } catch (Exception e) {

            log.error(e.getMessage() , e);
            log.info("[retrySmsSend][Exception] name : " + e.getClass().getName() + ",  " + " : " + e.getMessage() + " , cause : " + e.getCause());
            //9999
            smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                .flag(codeEtcException)
                .message(e.getMessage())
                .build();
        }
        return smsGatewayResponseDto;
    }

    private SmsGatewayResponseDto retryIfChecked(SmsAgentRequestDto smsAgentRequestDto, CheckRetryType checkRetry, SmsGatewayResponseDto smsGatewayResponseDto) {
        if (checkRetry == CheckRetryType.NO_RETRY || systemEr > retry || busyEr > busyRetry) {
            //재시도에 해당되지 않는 경우 or 재시도설정횟수보다 재시도한 횟수가 클 경우 or 메시지 처리 수용한계 설정횟수보다 처리 횟수가 클 경우
            return smsGatewayResponseDto;
        } else {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                throw new SmsAgentEtcException(messageEtcException);
            }

            return sendSms(smsAgentRequestDto);
        }
    }

    private CheckRetryType getCheckRetryType(CheckRetryType checkRetry, SmsGatewayResponseDto smsGatewayResponseDto) {
        if (smsGatewayResponseDto.getFlag().equals(codeSystemErrorException) || smsGatewayResponseDto.getFlag().equals(codeEtcException)) {
            // 시스템 장애이거나 기타오류 일 경우
            checkRetry = CheckRetryType.RETRY_CAUSE_ERROR;
            systemEr++;

        } else if (smsGatewayResponseDto.getFlag().equals(codeSystemBusyException)) {
            // 메시지 처리 수용 한계 초과일 경우
            checkRetry = CheckRetryType.RETRY_CAUSE_BUSY;
            busyEr++;
        }
        return checkRetry;
    }

    enum CheckRetryType {

        NO_RETRY,
        RETRY_CAUSE_ERROR,
        RETRY_CAUSE_BUSY
    }
}

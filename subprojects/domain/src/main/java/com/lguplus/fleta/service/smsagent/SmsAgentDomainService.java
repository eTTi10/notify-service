package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.SettingDomainClient;
import com.lguplus.fleta.client.SmsAgentClient;
import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.request.inner.SmsAgentRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.NotFoundMsgException;
import com.lguplus.fleta.exception.smsagent.SmsAgentCustomException;
import com.lguplus.fleta.exception.smsagent.SmsAgentEtcException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Optional;


/**
 * SMSAgentDomainService 설명
 * <p>
 * 최초작성 2021-11-23
 **/
@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class SmsAgentDomainService {

    private static final String SEP = "\\|";
    private final SettingDomainClient apiClient;
    private final SmsAgentClient smsAgentClient;
    @Value("${sms.agent.ignore.use}")
    private boolean agentNoSendUse;
    @Value("${sms.agent.ignore.time.from}")
    private int agentNoSendTimeFrom;
    @Value("${sms.agent.ignore.time.to}")
    private int agentNoSendTimeTo;
    @Value("${sms.send.retry}")
    private int smsRetry;
    @Value("${sms.send.busy.retry}")
    private int smsBusyRetry;
    @Value("${sms.sender.retry.sleep.ms}")
    private long smsSleepTime;
    @Value("${sms.error.etc.message}")
    private String messageEtcException;
    @Value("${sms.sender.number}")
    private String smsSenderNo;
    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemBusyException}")
    private String codeSystemBusyException;
    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemErrorException}")
    private String codeSystemErrorException;
    @Value("${sms.error.etc.flag}")
    private String codeEtcException;

    /**
     * 문자발송(문자내용으로발송)
     *
     * @param sendSmsRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto)  {

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
     *
     * @param sendSMSCodeRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSmsCode(SendSmsCodeRequestDto sendSMSCodeRequestDto) {

        //#########[LOG SET]#########
        log.debug("[smsCode] sendSMSCodeRequestDto - {}", sendSMSCodeRequestDto.toString());

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

        SmsSender smsSender = new SmsSender(smsAgentClient, this);

        return smsSender.sendSms(smsAgentRequestDto);

    }

    /**
     * API호출하여 리스트 중 원하는 문자내용만 리턴
     *
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
            CallSettingDto settingApi = callSettingApi.getResult().getData();

            //============ End [setting API 호CallSettingResultMapDto출 캐시등록] =============

            if (callSettingApi.getResult().getDataCount() > 0) {

                log.debug("sms_cd(메시지내용) {} ", settingApi.getName());
                return settingApi.getName();
            } else {
                return "";
            }

        } catch (Exception e) {
            log.debug("[callSettingApi][Call][" + e.getClass().getName() + "]" + e.getMessage());
            //9999
            throw new SmsAgentEtcException(messageEtcException);
        }
    }

}

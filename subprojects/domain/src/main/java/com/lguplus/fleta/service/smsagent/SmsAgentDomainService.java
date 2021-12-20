package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.request.inner.SmsAgentRequestDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
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

    @Value("${error.flag.com.lguplus.fleta.exception.smsagent.SystemErrorException}")
    private String codeSystemErrorException;

    @Value("${error.message.1500}")
    private String messageSystemError;

    /**
     * 문자발송(문자내용으로발송)
     * @param sendSmsRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSms(SendSmsRequestDto sendSmsRequestDto) {

        return SmsGatewayResponseDto.builder()
                .flag("0000")
                .message("성공")
                .build();
    }

    /**
     * 문자발송(코드로발송)
     * @param sendSMSCodeRequestDto
     * @return SmsGatewayResponseDto
     */
    public SmsGatewayResponseDto sendSmsCode(SendSmsCodeRequestDto sendSMSCodeRequestDto) {

        return SmsGatewayResponseDto.builder()
                .flag("0000")
                .message("성공")
                .build();

    }

}

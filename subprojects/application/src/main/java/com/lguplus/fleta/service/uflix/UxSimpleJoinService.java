package com.lguplus.fleta.service.uflix;

import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.request.outer.UxSimpleJoinSmsRequestDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.SmsAgentCustomException;
import com.lguplus.fleta.service.smsagent.SmsAgentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UxSimpleJoinService {

    private final SmsAgentDomainService smsAgentDomainService;

    @Value("${sms.uflix.simplejoin.sms_message}")
    private String smsMessage;

    /**
     * tvG 유플릭스 간편 가입 안내 SMS 요청
     *
     * @param uxSimpleJoinSmsRequestDto tvG 유플릭스 간편 가입 안내 SMS 요청을 위한 DTO
     */

    @SneakyThrows
    public SmsGatewayResponseDto requestUxSimpleJoinSms(UxSimpleJoinSmsRequestDto uxSimpleJoinSmsRequestDto) {
        log.debug("smsMessage ::::::::::::::::::::: {}", smsMessage);

        SendSmsRequestDto sendSmsRequestDto = SendSmsRequestDto.builder()
                .sCtn(uxSimpleJoinSmsRequestDto.getCtn())
                .rCtn(uxSimpleJoinSmsRequestDto.getCtn())
                .msg(smsMessage).build();

        // SMS 전송
        try {
            return smsAgentDomainService.sendSms(sendSmsRequestDto);

        } catch (SmsAgentCustomException ex) {
            return SmsGatewayResponseDto.builder().flag(ex.getCode()).message(ex.getMessage()).build();
        }
    }

}

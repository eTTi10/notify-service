package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.data.dto.request.SendSMSCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SMSAgentDomainService {


    /**
     *
     * 단순 SMS 전송
     * @param sendSMSRequestDto SMS전송 DTO
     * @return 단건푸시등록 결과
     */
    public SuccessResponseDto send(SendSMSRequestDto sendSMSRequestDto) {

        log.debug("SMSAgentDomainService.sendSMS() - {}:{}", "SMS발송 처리", sendSMSRequestDto);

        return SuccessResponseDto.builder().build();
    }

    /**
     *
     * 코드를 이용한 SMS 전송
     * @param sendSMSCodeRequestDto SMS전송 DTO
     * @return 단건푸시등록 결과
     */
    public SuccessResponseDto sendSMS(SendSMSCodeRequestDto sendSMSCodeRequestDto) {

        log.debug("SMSAgentDomainService.sendSMS() - {}:{}", "SMS발송 처리", sendSMSCodeRequestDto);

        return SuccessResponseDto.builder().build();
    }

}

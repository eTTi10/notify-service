package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MMSAgentDomainService {
    @Value("${mms.namespace}")
    private String mmsNamespace;

    public SuccessResponseDto sendMMS(SendMMSRequestDto sendMMSRequestDto) {
        log.debug(mmsNamespace+"<==============test");
        log.debug("MMSAgentDomainService.sendMMS() - {}:{}", "MMS발송 처리", sendMMSRequestDto);
        return SuccessResponseDto.builder().build();
    }

}

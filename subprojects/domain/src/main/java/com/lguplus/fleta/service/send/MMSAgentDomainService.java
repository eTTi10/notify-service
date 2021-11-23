package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MMSAgentDomainService {
    @Value("${mms.namespace}")
    private String mmsNamespace;

    public SuccessResponseDto sendMMS(SendMMSRequestDto sendMMSRequestDto) {
        log.debug(mmsNamespace+"<==============test");
        log.debug("MMSAgentDomainService.sendMMS() - {}:{}", "MMS발송 처리", sendMMSRequestDto);
        log.debug("MMSAgentDomainService.sendMMS() - {}:{}", "//setting API 호출하여 메세지 등록");
        //Map<String,String> redisCcallSettingApi();
        return SuccessResponseDto.builder().build();
    }

    /*
    //@Cacheable(cacheName="mmsMessageCache")
    public Map<String,String> callSettingApi() throws Exception{
        return
    }
    */

}

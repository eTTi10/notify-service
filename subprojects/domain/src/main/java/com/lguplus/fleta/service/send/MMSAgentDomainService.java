package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.CallSettingApiClient;
import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
//@AllArgsConstructor
//@NoArgsConstructor
//@RequestParam
@RequiredArgsConstructor
public class MMSAgentDomainService {
//mmsAgentDomastervice 찾을 수없는 'com.lgeplus.fleta.client.callsettingapiclient'유형의 bean이 필요했습니다.
    private final CallSettingApiClient aipClient;

    public SuccessResponseDto sendMMS(SendMMSRequestDto sendMMSRequestDto) {
        log.debug("MMSAgentDomainService.sendMMS() - {}:{}", "MMS발송 처리", sendMMSRequestDto);
        log.debug("MMSAgentDomainService.sendMMS() - {}:{}", "//setting API 호출하여 메세지 등록");

        Map<String, String> prm = new HashMap<>();
        prm.put("sa_id","mms");
        prm.put("stb_mac","mms");
        prm.put("code_id","");
        prm.put("svc_type","E");
       Map<String, Object> result = aipClient.callSettingApi(prm);
        return SuccessResponseDto.builder().build();
    }

    /*
    //@Cacheable(cacheName="mmsMessageCache")
    public Map<String,String> callSettingApi() throws Exception{
        return
    }
    */

}

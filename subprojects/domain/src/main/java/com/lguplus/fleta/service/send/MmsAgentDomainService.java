package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
//@AllArgsConstructor
//@NoArgsConstructor
//@RequestParam
@RequiredArgsConstructor
public class MmsAgentDomainService {
//mmsAgentDomastervice 찾을 수없는 'com.lgeplus.fleta.client.callsettingapiclient'유형의 bean이 필요했습니다.
    private final CallSettingDomainClient aipClient;

    public SuccessResponseDto sendMmsCode(SendMMSRequestDto sendMMSRequestDto) {
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

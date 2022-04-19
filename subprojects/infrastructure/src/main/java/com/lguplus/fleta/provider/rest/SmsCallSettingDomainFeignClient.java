package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.SmsCallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class SmsCallSettingDomainFeignClient implements SmsCallSettingDomainClient {

    private final SmsCallSettingFeignClient smsApi;

    /**
     * Sms메세지 목록
     * @return
     */
    @Override
//    @Cacheable(value="SMS_CACHE", key="'smsMessageCache'")
    public CallSettingResultMapDto smsCallSettingApi(CallSettingRequestDto parm){
        Map<String, String> parmMap = new HashMap<>();
        parmMap.put("code",parm.getCode());
        parmMap.put("svc_type",parm.getSvcType());
        return smsApi.callSettingApi(parmMap);
    }

}

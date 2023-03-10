package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.SettingDomainClient;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SettingDomainFeignClient implements SettingDomainClient {

    private final SettingFeignClient smsApi;

    /**
     * Sms메세지 목록
     *
     * @return
     */
    @Override
    //    @Cacheable(value="SMS_CACHE", key="'smsMessageCache'")
    public CallSettingResultMapDto callSettingApi(CallSettingRequestDto parm) {
        Map<String, String> parmMap = new HashMap<>();
        parmMap.put("code", parm.getCode());
        parmMap.put("serviceType", parm.getSvcType());
        return smsApi.callSettingApi(parmMap);
    }

}

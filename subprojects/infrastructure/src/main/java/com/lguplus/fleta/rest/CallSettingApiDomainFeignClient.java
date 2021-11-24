package com.lguplus.fleta.rest;

import com.lguplus.fleta.client.CallSettingApiClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class CallSettingApiDomainFeignClient implements CallSettingApiClient {
    private final CallSettingApiFeignClient api;
    /**
     *
     * @return
     */
    @Override
    public Map<String, Object> callSettingApi(Map<String, String> prm){
        return api.callSettingApi(prm);
    };
}

package com.lguplus.fleta.rest;

import com.lguplus.fleta.client.CallSettingDomainClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class CallSettingDomainFeignClient implements CallSettingDomainClient {
    private final CallSettingFeignClient api;
    /**
     *
     * @return
     */
    @Override
    public Map<String, Object> callSettingApi(Map<String, String> prm){
        return api.callSettingApi(prm);
    };
}

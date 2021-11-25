package com.lguplus.fleta.rest;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
    public Map<String, Object> callSettingApi(CallSettingRequestDto prm){
        Map<String, String> prms = new HashMap<>();
        prms.put("sa_id","mms");
        prms.put("stb_mac","mms");
        prms.put("code_id","M011");
        prms.put("svc_type","E");

        Map<String, Object> result = api.callSettingApi(prms);

        return result;
    };

}

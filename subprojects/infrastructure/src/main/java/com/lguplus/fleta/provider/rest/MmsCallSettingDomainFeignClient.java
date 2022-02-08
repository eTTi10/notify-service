package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.MmsCallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class MmsCallSettingDomainFeignClient implements MmsCallSettingDomainClient {

    private final MmsCallSettingFeignClient mmsApi;

    /**
     * Mms메세지 목록
     * @return
     */
    @Override
    //@Cacheable(value="MMS_CACHE", key="'mmsMessageCache'")
    public CallSettingResultMapDto mmsCallSettingApi(CallSettingRequestDto parm){
        Map<String, String> parmMap = new HashMap<>();
        parmMap.put("code",parm.getCode());
        parmMap.put("svc_type",parm.getSvcType());
        return mmsApi.callSettingApi(parmMap);
    }

}

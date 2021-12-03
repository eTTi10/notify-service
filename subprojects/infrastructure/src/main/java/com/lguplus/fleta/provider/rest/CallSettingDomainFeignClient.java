package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.cache.annotation.Cacheable;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Component
@AllArgsConstructor
public class CallSettingDomainFeignClient implements CallSettingDomainClient {
    private final MmsCallSettingFeignClient mmsApi;
    private final SmsCallSettingFeignClient smsApi;
    /**
     * Mms메세지 목록
     * @return
     */
    @Override
    @Cacheable(value="MMS_CACHE", key="'mmsMessageCache'")
    public CallSettingResultMapDto mmsCallSettingApi(CallSettingRequestDto parm){
        Map<String, String> parmMap = new HashMap<>();
        parmMap.put("sa_id",parm.getSaId());
        parmMap.put("stb_mac",parm.getStbMac());
        parmMap.put("code_id",parm.getCodeId());
        parmMap.put("svc_type",parm.getSvcType());
        CallSettingResultMapDto apiMap = mmsApi.callSettingApi(parmMap);
        return apiMap;
    };

    /**
     * Sms메세지 목록
     * @return
     */
    @Override
    @Cacheable(value="SMS_CACHE", key="'smsMessageCache'")
    public CallSettingResultMapDto smsCallSettingApi(CallSettingRequestDto parm){
        Map<String, String> parmMap = new HashMap<>();
        parmMap.put("sa_id",parm.getSaId());
        parmMap.put("stb_mac",parm.getStbMac());
        parmMap.put("code_id",parm.getCodeId());
        parmMap.put("svc_type",parm.getSvcType());
        CallSettingResultMapDto apiMap = smsApi.callSettingApi(parmMap);
        return apiMap;
    };

}

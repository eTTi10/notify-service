package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
@AllArgsConstructor
public class CallSettingDomainFeignClient implements CallSettingDomainClient {
    private final CallSettingFeignClient api;

    /**
     *
     * @return
     */
    @Override
    @Cacheable(value="PANEL_CACHE", key="'VERSION'")
    public CallSettingResultMapDto  callSettingApi(CallSettingRequestDto parm){
        Map<String, String> parmMap = new HashMap<>();
        parmMap.put("sa_id",parm.getSaId());
        parmMap.put("stb_mac",parm.getStbMac());
        parmMap.put("code_id",parm.getCodeId());
        parmMap.put("svc_type",parm.getSvcType());

        CallSettingResultMapDto apiMap = api.callSettingApi(parmMap);
       //CallSettingResultDto resultMap = (CallSettingResultDto) apiMap.get("result");
        //List<CallSettingDto> rs = (List<CallSettingDto>) resultMap.get("recordset");

        log.info("\n\n============ [ Start - callSettingApi 전송메세지 목록 ] ============\n\n");
        log.info(apiMap.toString());
        log.info("\n\n============ [ End - callSettingApi 전송메세지 목록 ] ============\n\n");


        return apiMap;
    };

}

package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public GenericRecordsetResponseDto<CallSettingDto> callSettingApi(CallSettingRequestDto parm){
        Map<String, String> parmMap = new HashMap<>();
        parmMap.put("sa_id",parm.getSaId());
        parmMap.put("stb_mac",parm.getStbMac());
        parmMap.put("code_id",parm.getCodeId());
        parmMap.put("svc_type",parm.getSvcType());

        Map<String, Object> apiMap = api.callSettingApi(parmMap);
        Map<String, Object> resultMap = (Map<String, Object>) apiMap.get("result");
        List<CallSettingDto> rs = (List<CallSettingDto>) resultMap.get("recordset");

        log.info("\n\n============ [ Start - callSettingApi 전송메세지 목록 ] ============\n\n");
        log.info(rs.toString());
        log.info("\n\n============ [ End - callSettingApi 전송메세지 목록 ] ============\n\n");

        return GenericRecordsetResponseDto.<CallSettingDto>genericRecordsetResponseBuilder()
                .totalCount(rs.size())
                .recordset(rs)
                .build();
    };

}

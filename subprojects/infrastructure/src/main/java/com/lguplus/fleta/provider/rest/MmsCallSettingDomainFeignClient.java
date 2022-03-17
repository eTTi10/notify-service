package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.MmsCallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultDto;
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
public class MmsCallSettingDomainFeignClient implements MmsCallSettingDomainClient {

    private final MmsCallSettingFeignClient mmsApi;

    /**
     * Mms메세지 목록
     * @return
     */
    @Override
    @Cacheable(value="MMS_CACHE", key="'mmsMessageCache'")
    public CallSettingResultMapDto mmsCallSettingApi(CallSettingRequestDto parm){
        Map<String, String> parmMap = new HashMap<>();
        parmMap.put("code",parm.getCode());
        parmMap.put("svc_type",parm.getSvcType());

        CallSettingDto data = CallSettingDto.builder()
                .code("M011")
                .name("U+아이들나라 테스트 메세지 입니다.")
                .serviceType("E")
                .etc("U+아이들나라 체험 상품 안내")
                .terminals(new String[]{})
                .build();
        CallSettingResultDto result = CallSettingResultDto.builder()
                .dataType("SINGLE")
                .dataCount(1)
                .data(data)
                .build();
        CallSettingResultMapDto dto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("정상")
                .result(result)
                .build();
        return dto;
        //return mmsApi.callSettingApi(parmMap);
    }

}

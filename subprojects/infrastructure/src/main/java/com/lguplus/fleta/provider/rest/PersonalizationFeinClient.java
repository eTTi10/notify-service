package com.lguplus.fleta.provider.rest;


import com.lguplus.fleta.data.dto.response.RegistrationIdResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

//@FeignClient(name="personalization", url="${service.personalization.url}")
@FeignClient(name="personalization", url="locahost:8080")
public interface PersonalizationFeinClient {

    @GetMapping(value="/personalization/registrationId", consumes = "application/json")
    InnerResponseDto<Map<String, RegistrationIdResponseDto>> getRegistrationID(@RequestBody Map<String, Map<String, String>> conditions);
}

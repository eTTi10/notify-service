package com.lguplus.fleta.provider.rest;


import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="personalization", url="${service.personalization.url}")
public interface PersonalizationFeinClient {

    @GetMapping(value="/personalization/registrationId", produces = "application/json", consumes = "application/json")
    InnerResponseDto<RegIdDto> getRegistrationID(@RequestParam Map<String, String> conditions);
}

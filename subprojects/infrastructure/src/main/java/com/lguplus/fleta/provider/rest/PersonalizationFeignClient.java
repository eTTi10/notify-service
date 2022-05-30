package com.lguplus.fleta.provider.rest;


import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "personalization", url = "${service.personalization.url}")
public interface PersonalizationFeignClient {

    @GetMapping(value = "/personalization/registrationid/info", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    InnerResponseDto<RegIdDto> getRegistrationID(@RequestParam Map<String, String> conditions);
}

package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(name = "settings", url = "${service.settings.url}")
public interface SettingFeignClient {
    /**
     *
     * @return
     */
    @GetMapping(value = "/settings/configuration", produces = "application/json")
    CallSettingResultMapDto callSettingApi(@RequestParam Map<String, String> prm);


}

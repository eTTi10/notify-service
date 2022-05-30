package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "settings", url = "${service.settings.url}")
public interface SettingFeignClient {

    /**
     * @return
     */
    @GetMapping(value = "/settings/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    CallSettingResultMapDto callSettingApi(@RequestParam Map<String, String> prm);


}

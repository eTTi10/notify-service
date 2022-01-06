package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(name = "mms-agent-rest-message", url = "${mmsroot.mms.setting.rest_url}")
public interface MmsCallSettingFeignClient {
    /**
     *
     * @return
     */
    @GetMapping(value = "${mmsroot.mms.setting.rest_path}", produces = "application/json")
    CallSettingResultMapDto callSettingApi(@RequestParam Map<String, String> prm);


}

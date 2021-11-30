package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(name = "sms-agent-rest-message", url = "${yml.mms.setting.rest_url}")
public interface SmsCallSettingFeignClient {
    /**
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "${yml.mms.setting.rest_path}", produces = "application/json")
    CallSettingResultMapDto callSettingApi(@RequestParam Map<String, String> prm);


}

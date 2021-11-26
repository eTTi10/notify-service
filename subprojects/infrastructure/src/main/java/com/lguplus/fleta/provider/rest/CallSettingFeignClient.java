package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(name = "mms-agent-rest-message", url = "${mms.setting.rest_url}")
public interface CallSettingFeignClient {
    /**
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "${mms.setting.rest_path}", produces = "application/json")
    CallSettingResultMapDto callSettingApi(@RequestParam Map<String, String> prm);


}

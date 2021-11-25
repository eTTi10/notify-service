package com.lguplus.fleta.rest;

import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "mms-agent-rest-message", url = "http://hdtv.suxm.uplus.co.kr")
public interface CallSettingFeignClient {
    /**
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/hdtv/comm/setting", produces = "application/json")
    public Map<String, Object> callSettingApi(@RequestParam Map<String, String> prm);


}

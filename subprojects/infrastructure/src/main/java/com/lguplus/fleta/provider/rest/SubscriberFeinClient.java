package com.lguplus.fleta.provider.rest;


import com.lguplus.fleta.data.dto.SaIdDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

//@FeignClient(name="subscriber", url="${service.subscriber.url}")
@FeignClient(name="subscriber", url="http://localhost:8084")
public interface SubscriberFeinClient {

    @GetMapping(value="/subscriber/subscriberByCtn", produces = "application/json", consumes = "application/json")
    InnerResponseDto<List<SaIdDto>> getRegistrationIDbyCtn(@RequestParam Map<String, String> conditions);
}

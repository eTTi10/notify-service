package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.SaIdDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "subscriber", url = "${service.subscriber.url}")
public interface SubscriberFeignClient {

    @GetMapping(value = "/subscriber/subscriberByCtn", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    InnerResponseDto<List<SaIdDto>> getRegistrationIDbyCtn(@RequestParam Map<String, String> conditions);
}

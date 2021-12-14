package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.config.HttpPushFeignConfig;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;
import java.util.Map;

/**
 * Http Push FeignClient (Open API 이용)
 *
 * 단건, 멀티, 공지 푸시등록
 */
@FeignClient(name = "httppush", url = "${singlepush.server.ip}", configuration = HttpPushFeignConfig.class)
public interface HttpPushFeignClient {

    /**
     * 단건 푸시
     *
     * @param baseUri uri 정보
     * @param paramMap 단건 푸시 정보
     * @return 단건 푸시 결과
     */
//    @PostMapping(value = "/settings/restapi/push/single/servicekey")
    @PostMapping(value = "/restapi/push/single/servicekey")
    OpenApiPushResponseDto requestHttpPushSingle(URI baseUri, @RequestHeader Map<String, String> headerMap, @RequestBody Map<String, Object> paramMap);

}

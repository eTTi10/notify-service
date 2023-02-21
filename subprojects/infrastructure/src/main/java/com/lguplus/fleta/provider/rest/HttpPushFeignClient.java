package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.config.HttpPushFeignConfig;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import java.net.URI;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Http Push FeignClient (Open API 이용)
 * <p>
 * 단건, 멀티(단건 사용), 공지 푸시등록
 */
@FeignClient(name = "httppush", url = "${push.openapi.single.server.ip}", configuration = HttpPushFeignConfig.class)
public interface HttpPushFeignClient {

    /**
     * 단건 푸시
     *
     * @param baseUri  uri 정보
     * @param paramMap 단건 푸시 정보
     * @return 단건 푸시 결과
     */
    @PostMapping(value = "/restapi/push/single/servicekey")
    OpenApiPushResponseDto requestHttpPushSingle(URI baseUri, @RequestBody Map<String, Object> paramMap);
//todo 인증서 에러 무시하는 기능 찾아서 할 것.
    /**
     * 공지 푸시
     *
     * @param baseUri  uri 정보
     * @param paramMap 공지 푸시 정보
     * @return 공지 푸시 결과
     */
    @PostMapping(value = "/restapi/push/announce")
    OpenApiPushResponseDto requestHttpPushAnnouncement(URI baseUri, @RequestBody Map<String, Object> paramMap);

}

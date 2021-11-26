package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Http Push Domain FeignClient (Open API 이용)
 *
 * 단건, 멀티, 공지 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpPushDomainFeignClient implements HttpPushDomainClient {

    private final HttpPushFeignClient httpPushFeignClient;

    @Value("${singlepush.server.ip}")
    private String host;

    @Value("${singlepush.server.protocol}")
    private String protocol;

    @Value("${singlepush.server.port1}")
    private String httpPort;

    @Value("${singlepush.server.port2}")
    private String httpsPort;

    @Value("${singlepush.server.auth}")
    private String authorization;


    /**
     * 단건 푸시
     *
     * @param paramMap 단건 푸시 정보
     * @return 단건 푸시 결과
     */
    @Override
    public OpenApiPushResponseDto requestHttpPushSingle(Map<String, String> paramMap) {
        log.debug("base url :::::::::::: {}", getBaseUrl());
        log.debug("header :::::::::::: {}", getHeaderMap());
        log.debug("paramMap :::::::::::: {}", paramMap);

        return httpPushFeignClient.requestHttpPushSingle(URI.create(getBaseUrl()), getHeaderMap(), paramMap);
    }

    /**
     * 기본 URL 을 가져온다.
     *
     * @return 기본 URL
     */
    private String getBaseUrl() {
        return protocol + "://" + host + ":" + (protocol.equals("http") ? httpPort : httpsPort);
    }

    /**
     * 기본 Header 정보를 가져온다.
     *
     * @return 기본 Header 정보
     */
    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headerMap.put(HttpHeaders.ACCEPT_CHARSET, "utf-8");
        headerMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headerMap.put(HttpHeaders.CONTENT_ENCODING, "utf-8");
        headerMap.put(HttpHeaders.AUTHORIZATION, authorization);

        return headerMap;
    }

}

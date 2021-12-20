package com.lguplus.fleta.provider.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.*;
import feign.FeignException;
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

    private final ObjectMapper objectMapper;

    @Value("${singlepush.server.ip}")
    private String hostSingle;

    @Value("${singlepush.server.protocol}")
    private String protocolSingle;

    @Value("${singlepush.server.port1}")
    private String httpPortSingle;

    @Value("${singlepush.server.port2}")
    private String httpsPortSingle;

    @Value("${announce.server.ip}")
    private String hostAnnounce;

    @Value("${announce.server.protocol}")
    private String protocolAnnounce;

    @Value("${announce.server.port1}")
    private String httpPortAnnounce;

    @Value("${announce.server.port2}")
    private String httpsPortAnnounce;


    /**
     * 단건 푸시
     *
     * @param paramMap 단건 푸시 정보
     * @return 단건 푸시 결과
     */
    @Override
    public OpenApiPushResponseDto requestHttpPushSingle(Map<String, Object> paramMap) {
//        log.debug("base url :::::::::::: {}", getBaseUrl("S"));
//        log.debug("header :::::::::::: {}", getHeaderMap("S"));

        /*try {
            log.debug("paramMap :::::::::::: \n{}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(paramMap));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/

        return httpPushFeignClient.requestHttpPushSingle(URI.create(getBaseUrl("S")), paramMap);
    }

    /**
     * 기본 URL 을 가져온다.
     *
     * @return 기본 URL
     */
    private String getBaseUrl(String kind) {
        // 단건, 멀티
        return protocolSingle + "://" + hostSingle + ":" + httpPortSingle;
    }

}

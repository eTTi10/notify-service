package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.PushAnnounceDomainClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.PushAnnounceResponseDto;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Push Announcement FeignClient
 *
 * 공지 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PushAnnounceDomainFeignClient implements PushAnnounceDomainClient {

    private final PushAnnounceFeignClient pushAnnounceFeignClient;
    private final PushConfig pushConfig;

    @Value("${push-comm.announce.server.ip}")
    private String host;

    @Value("${push-comm.announce.server.protocol}")
    private String protocol;

    @Value("${push-comm.announce.server.port}")
    private String port;

    /**
     * Push Announcement 푸시
     *
     * @param paramMap Push Announcement 푸시 정보
     * @return Push Announcement 푸시 결과
     */
    @Override
    public PushAnnounceResponseDto requestAnnouncement(Map<String, String> paramMap) {
        log.debug("requestAnnouncement:paramMap :::::::::::: {}", paramMap);
        log.debug("base url :::::::::::: {}", getBaseUrl(paramMap.get("service_id")));

        Map<String, Map<String, String>> sendMap = new HashMap<>();
        sendMap.put("request", paramMap);

        return pushAnnounceFeignClient.requestAnnouncement(URI.create(getBaseUrl(paramMap.get("service_id"))), sendMap);
    }

    /**
     * 기본 URL 을 가져온다.
     *
     * @return 기본 URL
     */
    private String getBaseUrl(String serviceId) {
        return this.protocol + "://" + getServiceServerIp(serviceId) + ":" + this.port;
    }

    // Announcement(별도서버 구성 시)
    private String getServiceServerIp(String serviceId) {
        String svcServerIp = pushConfig.getCommPropValue(serviceId + ".announce.server.ip");
        return svcServerIp == null ? this.host : svcServerIp;
    }

    /**
     * 기본 Header 정보를 가져온다.
     *
     * @return 기본 Header 정보
     */
    /*
    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(HttpHeaders.ACCEPT, this.header);
        headerMap.put(HttpHeaders.ACCEPT_CHARSET, this.encoding);
        headerMap.put(HttpHeaders.CONTENT_TYPE, this.header);
        headerMap.put(HttpHeaders.CONTENT_ENCODING, this.encoding);
       // headerMap.put(HttpHeaders.AUTHORIZATION, authorization);

        return headerMap;
    }
    */

}

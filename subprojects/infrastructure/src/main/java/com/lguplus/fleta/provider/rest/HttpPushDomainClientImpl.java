package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

/**
 * Http Push Domain FeignClient (Open API 이용)
 *
 * 단건, 멀티(단건 사용), 공지 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpPushDomainClientImpl implements HttpPushDomainClient {

    private final HttpPushFeignClient httpPushFeignClient;

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
        return httpPushFeignClient.requestHttpPushSingle(URI.create(getBaseUrl("S")), paramMap);
    }

    /**
     * 공지 푸시
     *
     * @param paramMap 공지 푸시 정보
     * @return 공지 푸시 결과
     */
    @Override
    public OpenApiPushResponseDto requestHttpPushAnnouncement(Map<String, Object> paramMap) {
        return httpPushFeignClient.requestHttpPushAnnouncement(URI.create(getBaseUrl("A")), paramMap);
    }

    /**
     * 기본 URL 을 가져온다.
     *
     * @return 기본 URL
     */
    private String getBaseUrl(String kind) {
        // 단건, 멀티
        if (kind.equals("S")) {
            return protocolSingle + "://" + hostSingle + ":" + (protocolSingle.equals("http") ? httpPortSingle : httpsPortSingle);

        // 공지
        } else {
            return protocolAnnounce + "://" + hostAnnounce + ":" + (protocolAnnounce.equals("http") ? httpPortAnnounce : httpsPortAnnounce);
        }
    }

}

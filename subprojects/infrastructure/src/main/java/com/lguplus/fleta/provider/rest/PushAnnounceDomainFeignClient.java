package com.lguplus.fleta.provider.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.client.PushAnnounceDomainClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.response.inner.PushAnnouncementResponseDto;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
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
    private final ObjectMapper objectMapper;

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
    public PushAnnouncementResponseDto requestAnnouncement(Map<String, String> paramMap) {
        //log.debug("requestAnnouncement:paramMap :::::::::::: {}", paramMap);
        //log.debug("base url :::::::::::: {}", getBaseUrl(paramMap.get("service_id")));

        Map<String, Map<String, String>> sendMap = new HashMap<>();
        sendMap.put("request", paramMap);

        try {
            Map<String,Object> retMap = pushAnnounceFeignClient.requestAnnouncement(URI.create(getBaseUrl(paramMap.get("service_id"))), sendMap);
            Map<String,Object> stateMap = (Map<String,Object>)retMap.get("response");
            return objectMapper.convertValue(stateMap, PushAnnouncementResponseDto.class);
        }
        catch (RetryableException ex) {
            log.debug(":::::::::::::::::::: RetryableException Read Timeout :: <{}>", ex.toString());
            return PushAnnouncementResponseDto.builder().statusCode("5102").build();
        }
        catch (FeignException ex) {
            log.debug("ex.contentUTF8() ::::::::::::::::::::::::: {}", ex.contentUTF8());

            try {
                Map<String,Object> retMap = objectMapper.readValue(ex.contentUTF8(),  new TypeReference<Map<String,Object>>(){});
                Map<String,Object> stateMap = (Map<String,Object>)retMap.get("response");
                return objectMapper.convertValue(stateMap, PushAnnouncementResponseDto.class);
            } catch (JsonProcessingException e) {
                return PushAnnouncementResponseDto.builder().statusCode("5103").build();
            }
        }
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
        //test
        //return "localhost";

        String svcServerIp = pushConfig.getCommPropValue(serviceId + ".announce.server.ip");
        return svcServerIp == null ? this.host : svcServerIp;
    }

}

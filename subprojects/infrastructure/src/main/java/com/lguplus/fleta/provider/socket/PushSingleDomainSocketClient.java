package com.lguplus.fleta.provider.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.client.PushSingleDomainClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.response.inner.PushSingleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
public class PushSingleDomainSocketClient implements PushSingleDomainClient {

    private final PushSingleSocketClient pushSingleSocketClient;
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
    public PushSingleResponseDto requestPushSingle(Map<String, String> paramMap) {
        //log.debug("requestAnnouncement:paramMap :::::::::::: {}", paramMap);
        //log.debug("base url :::::::::::: {}", getBaseUrl(paramMap.get("service_id")));

        Map<String, Map<String, String>> sendMap = new HashMap<>();
        sendMap.put("request", paramMap);
        try {
            log.debug("requestAnnouncement:sendMap :::::::::::: {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(sendMap));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new PushSingleResponseDto();

        //return pushAnnounceFeignClient.requestAnnouncement(URI.create(getBaseUrl(paramMap.get("service_id"))), sendMap);
    }

}

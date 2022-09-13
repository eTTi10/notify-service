package com.lguplus.fleta.provider.rest;

import java.net.URI;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Push Announcement FeignClient
 * <p>
 * 공지 푸시등록
 */
@FeignClient(name = "pushannounce", url = "$")
public interface PushAnnounceFeignClient {

    /**
     * Announcement 푸시
     *
     * @param baseUri  uri 정보
     * @param paramMap 푸시 정보
     * @return 푸시 결과
     */
    @PostMapping(value = "${push.gateway.announce.server.url}")
    Map<String, Object> requestAnnouncement(URI baseUri, @RequestBody Map<String, Map<String, String>> paramMap);

}

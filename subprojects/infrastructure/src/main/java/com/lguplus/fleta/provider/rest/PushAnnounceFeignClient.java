package com.lguplus.fleta.provider.rest;

import feign.RetryableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.util.Map;

/**
 * Push Announcement FeignClient
 *
 * 공지 푸시등록
 */
@FeignClient(name = "pushannounce", url = "$")
public interface PushAnnounceFeignClient {
     /**
     * Announcement 푸시
     *
     * @param baseUri uri 정보
     * @param paramMap 푸시 정보
     * @return 푸시 결과
     */
    @PostMapping(value = "${push-comm.announce.server.url}")
    Map<String, Object> requestAnnouncement(URI baseUri, @RequestBody Map<String, Map<String, String>> paramMap);//throws RetryableException;

}

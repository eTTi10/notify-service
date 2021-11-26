package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.response.inner.PushAnnounceResponseDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;
import java.util.Map;

/**
 * Push Announcement FeignClient
 *
 * 공지 푸시등록
 */
@FeignClient(name = "pushannounce", url = "$")
@Headers({
        "Content-Type: ${push-comm.announce.server.header}",
        "Accept: ${push-comm.announce.server.header}",
        "Accept-Charset: ${push-comm.announce.server.encoding}",
        "Content-Encoding: ${push-comm.announce.server.encoding}"
})
public interface PushAnnounceFeignClient {
//PushSingleSocketClient
    //PushSingleDomainService	requestPushSingle	Socket	PushSingleSocketClient.requestPushSingle()
    /**
     * Announcement 푸시
     *
     * @param baseUri uri 정보
     * @param paramMap 푸시 정보
     * @return 푸시 결과
     */
    @PostMapping(value = "${push-comm.announce.server.url}")
    PushAnnounceResponseDto requestAnnouncement(URI baseUri, @RequestBody Map<String, Map<String, String>> paramMap);

}

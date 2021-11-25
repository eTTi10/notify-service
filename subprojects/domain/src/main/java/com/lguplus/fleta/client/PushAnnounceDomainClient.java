package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.PushAnnounceResponseDto;

import java.util.Map;

/**
 * Push Announcement FeignClient
 *
 * 공지 푸시등록
 */
public interface PushAnnounceDomainClient {

    /**
     * Push Announcement
     *
     * @param paramMap Push Announcement 정보
     * @return Push Announcement 결과
     */
    PushAnnounceResponseDto requestAnnouncement(Map<String, String> paramMap);

}

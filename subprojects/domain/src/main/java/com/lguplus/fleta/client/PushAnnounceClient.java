package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import java.util.Map;

/**
 * Push Announcement FeignClient
 * <p>
 * 공지 푸시등록
 */
public interface PushAnnounceClient {

    /**
     * Push Announcement
     *
     * @param paramMap Push Announcement 정보
     * @return Push Announcement 결과
     */
    PushResponseDto requestAnnouncement(Map<String, String> paramMap);

}

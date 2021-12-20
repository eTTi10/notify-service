package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;

import java.util.Map;

/**
 * Push 단건 Socket client
 *
 * 공지 푸시등록
 */
public interface PushSingleClient {

    /**
     * Push Announcement
     *
     * @param paramMap Push 단건 정보
     * @return Push 단건 결과
     */
    PushResponseDto requestPushSingle(Map<String, String> paramMap);

    PushStatDto getPushStatus(String serviceId, long measurePushCount, long measureStartMillis);

    PushStatDto putPushStatus(String serviceId, long measurePushCount, long measureStartMillis);

}

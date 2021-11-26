package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.inner.PushSingleResponseDto;

import java.util.Map;

/**
 * Push 단건 Socket client
 *
 * 공지 푸시등록
 */
public interface PushSingleDomainClient {

    /**
     * Push Announcement
     *
     * @param paramMap Push 단건 정보
     * @return Push 단건 결과
     */
    PushSingleResponseDto requestPushSingle(Map<String, String> paramMap);

}

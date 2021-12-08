package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.inner.PushSingleResponseDto;

import java.util.Map;

/**
 * Push Multi Socket client
 *
 * 공지 푸시등록
 */
public interface PushMultiClient {

    /**
     * Push Announcement
     *
     * @param paramMap Push Multi 정보
     * @return Push Multi 결과
     */
    PushSingleResponseDto requestPushMulti(Map<String, String> paramMap);

}

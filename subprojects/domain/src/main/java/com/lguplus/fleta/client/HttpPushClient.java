package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import java.util.Map;

/**
 * Http Push FeignClient (Open API 이용)
 * <p>
 * 단건, 멀티(단건 사용), 공지 푸시등록
 */
public interface HttpPushClient {

    /**
     * 단건 푸시
     *
     * @param paramMap 단건 푸시 정보
     * @return 단건 푸시 결과
     */
    OpenApiPushResponseDto requestHttpPushSingle(Map<String, Object> paramMap);

    /**
     * 공지 푸시
     *
     * @param paramMap 공지 푸시 정보
     * @return 공지 푸시 결과
     */
    OpenApiPushResponseDto requestHttpPushAnnouncement(Map<String, Object> paramMap);

}

package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;

import java.util.Map;

/**
 * Http Push FeignClient (Open API 이용)
 *
 * 단건, 멀티(단건 사용), 공지 푸시등록
 */
public interface HttpPushDomainClient {

    /**
     * 단건 푸시
     *
     * @param paramMap 단건 푸시 정보
     * @return 단건 푸시 결과
     */
    OpenApiPushResponseDto requestHttpPushSingle(Map<String, Object> paramMap);

}

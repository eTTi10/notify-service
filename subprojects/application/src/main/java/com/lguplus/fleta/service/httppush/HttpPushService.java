package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Http Push Service
 *
 * 단건, 멀티, 공지 푸시등록
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HttpPushService {

    private final HttpPushDomainService httpPushDomainService;

    private final HttpMultiPushDomainService httpMultiPushDomainService;


    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushSingle(HttpPushSingleRequestDto httpPushSingleRequestDto) {
        return httpPushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);
    }

    /**
     * 멀티푸시등록
     *
     * @param httpPushMultiRequestDto 멀티푸시등록을 위한 DTO
     * @return 멀티푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushMulti(HttpPushMultiRequestDto httpPushMultiRequestDto) {
        return httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
    }

}

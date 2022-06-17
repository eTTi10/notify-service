package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Http MultiPush Service
 *
 * 멀티 푸시등록
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HttpMultiPushService {

    private final HttpMultiPushDomainService httpMultiPushDomainService;


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

package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushAnnounceRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Http Push Announce Service
 *
 * 공지 푸시등록
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HttpPushAnnounceService {

    private final HttpPushAnnounceDomainService httpPushAnnounceDomainService;

    /**
     * 공지푸시등록
     *
     * @param httpPushAnnounceRequestDto 공지푸시등록을 위한 DTO
     * @return 공지푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushAnnounce(HttpPushAnnounceRequestDto httpPushAnnounceRequestDto) {
        return httpPushAnnounceDomainService.requestHttpPushAnnounce(httpPushAnnounceRequestDto);
    }

}

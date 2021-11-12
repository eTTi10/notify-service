package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpPushSingleService {

    private final HttpPushSingleDomainService httpPushSingleDomainService;

    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public String requestHttpPushSingle(HttpPushSingleRequestDto httpPushSingleRequestDto) {
        return httpPushSingleDomainService.requestHttpPushSingle(httpPushSingleRequestDto);
    }

}

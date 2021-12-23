package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
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


    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushSingle(HttpPushSingleRequestDto httpPushSingleRequestDto) {
        /*try {
            return httpPushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);

        } catch (HttpPushCustomException ex) {
            log.debug("code ::::::::::::::: {}\tmessage :::::::::::::: {}", ex.getCode(), ex.getMessage());

            throw ex;
        }*/

        return httpPushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);
    }

}

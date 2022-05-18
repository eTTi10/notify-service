package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushAnnounceRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.util.HttpPushSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Http AnnouncementPush Component
 *
 * 공지 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpAnnouncementPushDomainService {

    private final HttpPushClient httpPushClient;

    private final HttpPushSupport httpPushSupport;


    /**
     * 공지푸시등록
     *
     * @param httpPushAnnounceRequestDto 공지푸시등록을 위한 DTO
     * @return 공지푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushAnnouncement(HttpPushAnnounceRequestDto httpPushAnnounceRequestDto) {
        log.debug("httpPushAnnounceRequestDto ::::::::::::::: {}", httpPushAnnounceRequestDto);

        String applicationId = httpPushAnnounceRequestDto.getApplicationId();
        String serviceId = httpPushAnnounceRequestDto.getServiceId();
        String pushType = httpPushAnnounceRequestDto.getPushType();
        String message = httpPushAnnounceRequestDto.getMessage();
        List<String> items = httpPushAnnounceRequestDto.getItems();

        Map<String, Object> paramMap = httpPushSupport.makePushParameters(applicationId, serviceId, pushType, message, items);

        OpenApiPushResponseDto openApiPushResponseDto = httpPushClient.requestHttpPushAnnouncement(paramMap);
        log.debug("openApiPushResponseDto ::::::::::::::: {}", openApiPushResponseDto);

        // 성공
        return HttpPushResponseDto.builder().build();
    }

}

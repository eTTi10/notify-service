package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushDomainClient;
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
 * Http Push Announce Component
 *
 * 공지 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpPushAnnounceDomainService {

    private final HttpPushDomainClient httpPushDomainClient;

    private final HttpPushSupport httpPushSupport;


    /**
     * 공지푸시등록
     *
     * @param httpPushAnnounceRequestDto 공지푸시등록을 위한 DTO
     * @return 공지푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushAnnounce(HttpPushAnnounceRequestDto httpPushAnnounceRequestDto) {
        log.debug("httpPushAnnounceRequestDto ::::::::::::::: {}", httpPushAnnounceRequestDto);

//        httpServiceProps.getKeys().forEach(m -> log.debug(m.toString()));

        String appId = httpPushAnnounceRequestDto.getAppId();
        String serviceId = httpPushAnnounceRequestDto.getServiceId();
        String pushType = httpPushAnnounceRequestDto.getPushType();
        String msg = httpPushAnnounceRequestDto.getMsg();
        List<String> items = httpPushAnnounceRequestDto.getItems();

        Map<String, Object> paramMap = httpPushSupport.makePushParameters(appId, serviceId, pushType, msg, items);

        OpenApiPushResponseDto openApiPushResponseDto = httpPushDomainClient.requestHttpPushAnnounce(paramMap);

        // 성공
        if (openApiPushResponseDto.getReturnCode().equals("200")) {
            return HttpPushResponseDto.builder().build();
        }

        // 실패
        return HttpPushResponseDto.builder()
                .code(openApiPushResponseDto.getReturnCode())
                .message(openApiPushResponseDto.getError().get("MESSAGE"))
                .build();
    }

}

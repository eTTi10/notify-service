package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.ExclusionNumberException;
import com.lguplus.fleta.util.HttpPushSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.*;

/**
 * Http Push Component
 *
 * 단건, 멀티 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpPushDomainService {

    private final HttpPushDomainClient httpPushDomainClient;

    private final HttpPushSupport httpPushSupport;

    private final Object lock = new Object();

    @Value("${multi.push.max.tps}")
    private String maxMultiCount;

    @Value("${multi.push.reject.regList}")
    private String exception;

    @Value("${error.flag.com.lguplus.fleta.exception.push.SendingFailedException}")
    private String sendingFailedExceptionCode;

    @Value("${error.message.1130}")
    private String sendingFailedExceptionMsg;

    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushSingle(HttpPushSingleRequestDto httpPushSingleRequestDto) {
        log.debug("httpPushSingleRequestDto ::::::::::::::: {}", httpPushSingleRequestDto);

//        httpServiceProps.getKeys().forEach(m -> log.debug(m.toString()));

        // 발송 제외 가번 확인
        log.debug("exception :::::::::::::::::::: {}", exception);
        String[] exceptionList = exception.split("\\|");
        String regId = httpPushSingleRequestDto.getUsers().get(0);

        if (Arrays.asList(exceptionList).contains(regId.strip())) {
            throw new ExclusionNumberException();   // 9998 발송제한번호
        }

        String appId = httpPushSingleRequestDto.getAppId();
        String serviceId = httpPushSingleRequestDto.getServiceId();
        String pushType = httpPushSingleRequestDto.getPushType();
        String msg = httpPushSingleRequestDto.getMsg();
        List<String> items = httpPushSingleRequestDto.getItems();

        Map<String, Object> paramMap = httpPushSupport.makePushParameters(appId, serviceId, pushType, msg, regId, items);

        httpPushDomainClient.requestHttpPushSingle(paramMap);

        // 성공
        return HttpPushResponseDto.builder().build();
    }

}
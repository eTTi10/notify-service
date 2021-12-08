package com.lguplus.fleta.provider.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Http Push Domain FeignClient (Open API 이용)
 *
 * 단건, 멀티, 공지 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpPushDomainFeignClient implements HttpPushDomainClient {

    private final HttpPushFeignClient httpPushFeignClient;

    private final ObjectMapper objectMapper;

    @Value("${singlepush.server.ip}")
    private String hostSingle;

    @Value("${singlepush.server.protocol}")
    private String protocolSingle;

    @Value("${singlepush.server.port1}")
    private String httpPortSingle;

    @Value("${singlepush.server.port2}")
    private String httpsPortSingle;

    @Value("${singlepush.server.auth}")
    private String authorizationSingle;

    @Value("${announce.server.ip}")
    private String hostAnnounce;

    @Value("${announce.server.protocol}")
    private String protocolAnnounce;

    @Value("${announce.server.port1}")
    private String httpPortAnnounce;

    @Value("${announce.server.port2}")
    private String httpsPortAnnounce;

    @Value("${announce.server.auth}")
    private String authorizationAnnounce;


    /**
     * 단건 푸시
     *
     * @param paramMap 단건 푸시 정보
     * @return 단건 푸시 결과
     */
    @Override
    public OpenApiPushResponseDto requestHttpPushSingle(Map<String, Object> paramMap) {
//        log.debug("base url :::::::::::: {}", getBaseUrl("S"));
//        log.debug("header :::::::::::: {}", getHeaderMap("S"));
        try {
            log.debug("paramMap :::::::::::: \n{}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(paramMap));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            return httpPushFeignClient.requestHttpPushSingle(URI.create(getBaseUrl("S")), getHeaderMap("S"), paramMap);

        } catch (FeignException ex) {
            log.debug("ex.contentUTF8() ::::::::::::::::::::::::: {}", ex.contentUTF8());

            try {
                return objectMapper.readValue(ex.contentUTF8(), new TypeReference<OpenApiPushResponseDto>(){});

            } catch (JsonProcessingException e) {
                switch (ex.status()) {
                    case HttpStatus.SC_ACCEPTED:
                        // code "1112", message "The request Accepted"
                        throw new AcceptedException();

                    case HttpStatus.SC_BAD_REQUEST:
                        // code "1104", message "Push GW BadRequest"
                        throw new BadRequestException();

                    case HttpStatus.SC_UNAUTHORIZED:
                        // code "1105", message "Push GW UnAuthorized"
                        throw new UnAuthorizedException();

                    case HttpStatus.SC_FORBIDDEN:
                        // code "1106", message "Push GW Forbidden"
                        throw new ForbiddenException();

                    case HttpStatus.SC_NOT_FOUND:
                        // code "1107", message "Push GW Not Found"
                        throw new NotFoundException();

                    // 메시지 전송 실패 - Retry 대상
                    default:
                        break;
                }
            }
        }

        return null;
    }

    /**
     * 기본 URL 을 가져온다.
     *
     * @return 기본 URL
     */
    private String getBaseUrl(String kind) {
        // 단건, 멀티
        if (kind.equals("S")) {
            return protocolSingle + "://" + hostSingle + ":" + (protocolSingle.equals("http") ? httpPortSingle : httpsPortSingle);
        }

        return null;
    }

    /**
     * 기본 Header 정보를 가져온다.
     *
     * @return 기본 Header 정보
     */
    private Map<String, String> getHeaderMap(String kind) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headerMap.put(HttpHeaders.ACCEPT_CHARSET, "utf-8");
        headerMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headerMap.put(HttpHeaders.CONTENT_ENCODING, "utf-8");
        headerMap.put(HttpHeaders.AUTHORIZATION, kind.equals("S") ? authorizationSingle : authorizationAnnounce);

        return headerMap;
    }

}

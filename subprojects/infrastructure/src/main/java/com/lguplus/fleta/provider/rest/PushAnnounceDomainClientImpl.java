package com.lguplus.fleta.provider.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.client.PushAnnounceDomainClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.data.mapper.PushMapper;
import com.lguplus.fleta.exception.push.*;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Push Announcement FeignClient
 *
 * 공지 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PushAnnounceDomainClientImpl implements PushAnnounceDomainClient {

    private final PushAnnounceFeignClient pushAnnounceFeignClient;
    private final PushConfig pushConfig;
    private final ObjectMapper objectMapper;
    private final PushMapper pushMapper;

    @Value("${push-comm.announce.server.ip}")
    private String host;

    @Value("${push-comm.announce.server.protocol}")
    private String protocol;

    @Value("${push-comm.announce.server.port}")
    private String port;

    /**
     * Push Announcement 푸시
     *
     * @param paramMap Push Announcement 푸시 정보
     * @return Push Announcement 푸시 결과
     */
    @Override
    public PushResponseDto requestAnnouncement(Map<String, String> paramMap) {

        Map<String, Map<String, String>> sendMap = new HashMap<>();
        sendMap.put("request", paramMap);
        try {
            Map<String,Object> retMap = pushAnnounceFeignClient.requestAnnouncement(URI.create(getBaseUrl(paramMap.get("service_id"))), sendMap);
            Map<String,String> stateMap = (Map<String,String>)retMap.get("response");

            //String json = objectMapper.writeValueAsString(stateMap)
            //log.debug("==json:" + json)

            return pushMapper.toResponseDto(stateMap);
        }
        catch (RetryableException ex) {
            log.debug(":::::::::::::::::::: RetryableException Read Timeout :: <{}>", ex.toString());
            throw new SocketTimeException();
        }

    }

    /**
     * 기본 URL 을 가져온다.
     *
     * @return 기본 URL
     */
    private String getBaseUrl(String serviceId) {
        return this.protocol + "://" + getServiceServerIp(serviceId) + ":" + this.port;
    }

    // Announcement(별도서버 구성 시)
    private String getServiceServerIp(String serviceId) {
        //test
        //return "localhost"

        String svcServerIp = pushConfig.getCommPropValue(serviceId + ".announce.server.ip");
        return svcServerIp == null ? this.host : svcServerIp;
    }

    public static class PushErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            FeignException ex = FeignException.errorStatus(methodKey, response);

            if(ex instanceof FeignException.FeignServerException) {
                //500 : InternalServerError
                //501 : NotImplemented
                //502 : BadGateway
                //503 : ServiceUnavailable
                //504 : GatewayTimeout

                switch (ex.status()) {
                    case 500:
                        return new InternalErrorException(ex);
                    case 502:
                        return new ExceptionOccursException(ex);
                    case 503:
                        return new ServiceUnavailableException(ex);
                    default:
                        break;
                }

            }
            else if(ex instanceof FeignException.FeignClientException) {
                //400 : BadRequest
                //401 : Unauthorized
                //403 : Forbidden
                //404 : NotFound
                //405 : MethodNotAllowed
                //406 : NotAcceptable
                //409 : Conflict
                //410 : Gone
                //415 : UnsupportedMediaType
                //429 : TooManyRequests
                //422 : UnprocessableEntity

                switch (ex.status()) {
                    case 400:
                        return new BadRequestException(ex);
                    case 401:
                        return new UnAuthorizedException(ex);
                    case 403:
                        return new ForbiddenException(ex);
                    case 404:
                        return new NotFoundException(ex);
                    case 410:
                        return new NotExistRegistIdException(ex);
                    case 412:
                        return new PreConditionFailedException(ex);
                    default:
                        break;
                }
            }
            else {
                if(202 == ex.status()) {
                    return new AcceptedException(ex);
                }
            }

            return new PushEtcException(ex);
        }
    }
}

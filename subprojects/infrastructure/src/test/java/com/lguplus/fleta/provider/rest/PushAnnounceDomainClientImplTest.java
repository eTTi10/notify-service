package com.lguplus.fleta.provider.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.data.mapper.PushMapper;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.push.*;
import feign.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PushAnnounceDomainClientImplTest {

    @InjectMocks
    private PushAnnounceDomainClientImpl pushAnnounceDomainClientImpl;

    @Mock
    private PushAnnounceFeignClient pushAnnounceFeignClient;

    @Mock
    private PushConfig pushConfig;

    @Mock
    private PushMapper pushMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    Map<String, String> paramMap;
    String jsonNormal;

    ////////////////////
    PushAnnounceDomainClientImpl.PushErrorDecoder pushErrorDecoder = new PushAnnounceDomainClientImpl.PushErrorDecoder();

    NotifyPushRuntimeException thrown;
    final Map<String, Collection<String>> headers = new LinkedHashMap<>();
    final Request request = Request.create(Request.HttpMethod.POST, "/test", Collections.emptyMap(), null, Util.UTF_8, null);


    @BeforeEach
    void setUp() {
        pushAnnounceDomainClientImpl = new PushAnnounceDomainClientImpl(pushAnnounceFeignClient, pushConfig, objectMapper, pushMapper);

        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        paramMap = new HashMap<>();
        paramMap.put("msg_id", "PUSH_ANNOUNCEMENT");
        paramMap.put("push_id", DateFormatUtils.format(new Date(), "yyyyMMdd") + "0001");
        paramMap.put("service_id", "lguplushdtvgcm");
        paramMap.put("app_id", "30011");
        paramMap.put("noti_contents", "\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"");
        paramMap.put("service_passwd", "servicePwd");

        items.forEach(e -> {
            String[] item = e.split("\\!\\^");
            if (item.length >= 2) {
                paramMap.put(item[0], item[1]);
            }
        });

        jsonNormal = "{\"response\" : {\"msg_id\" : \"PUSH_ANNOUNCEMENT\",\"push_id\" : \"202112140001\",\"status_code\" : \"201\",\"status_msg\" : \"OK\"}}";

    }


    //@Disabled("")
    @Test
    void requestAnnouncement()  {

        Map<String, Object>  retMap = new HashMap<>();
        Map<String, String>  contMap = new HashMap<>();
        contMap.put("msg_id", "PUSH_ANNOUNCEMENT");
        contMap.put("push_id", "202112080002");
        contMap.put("status_code", "200");
        contMap.put("status_msg", "OK");

        retMap.put("response", contMap);
        //{msg_id=PUSH_ANNOUNCEMENT, push_id=202112080002, status_code=200, status_msg=OK}

        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willReturn(retMap);

        PushResponseDto mockDto = PushResponseDto.builder().statusCode("200").build();
        given(pushMapper.toResponseDto(anyMap())).willReturn(mockDto);

        PushResponseDto responseDto = pushAnnounceDomainClientImpl.requestAnnouncement(paramMap);

        Assertions.assertEquals("200", responseDto.getStatusCode());
    }

    //@Test
    void requestAnnouncement_ex1()  {
        FeignException ex = new FeignExceptionEx(("<" + jsonNormal).getBytes(StandardCharsets.UTF_8));
        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willThrow(ex);
        PushResponseDto responseDto = pushAnnounceDomainClientImpl.requestAnnouncement(paramMap);
        Assertions.assertEquals("5103", responseDto.getStatusCode());
    }

   // @Test
    void requestAnnouncement_ex2()  {
        FeignException ex = new FeignExceptionEx(jsonNormal.getBytes(StandardCharsets.UTF_8));
        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willThrow(ex);

        PushResponseDto mockDto = PushResponseDto.builder().statusCode("201").build();
        given(pushMapper.toResponseDto(anyMap())).willReturn(mockDto);

        PushResponseDto  responseDto = pushAnnounceDomainClientImpl.requestAnnouncement(paramMap);
        Assertions.assertEquals("201", responseDto.getStatusCode());
    }

    @Test
    void requestAnnouncement_ex3()  {
        Map<String, Collection<String>> headers = new HashMap<>();
        Request request = feign.Request.create(Request.HttpMethod.POST, "localhost:8080", headers, "---".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8, null);

        RetryableException ex = new RetryableExceptionEx(request);
        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willThrow(ex);

        Exception thrown = assertThrows(SocketTimeException.class, () -> {
            pushAnnounceDomainClientImpl.requestAnnouncement(paramMap);
        });

        Assertions.assertTrue(thrown instanceof SocketTimeException);
    }

    static class FeignExceptionEx extends FeignException {
        protected FeignExceptionEx( byte[] responseBody) {
            super(0, "-", responseBody);
        }
    }

    static class RetryableExceptionEx extends RetryableException {
        public RetryableExceptionEx(Request request) {
            super(0, "-", Request.HttpMethod.POST, null, request);
        }
    }

    @Test
    void testServerException() {

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(500)); });
        Assertions.assertTrue(thrown instanceof InternalErrorException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(502)); });
        Assertions.assertTrue(thrown instanceof ExceptionOccursException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(503)); });
        Assertions.assertTrue(thrown instanceof ServiceUnavailableException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(504)); });
        Assertions.assertTrue(thrown instanceof PushEtcException);

    }

    @Test
    void testClientException() {

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(400)); });
        Assertions.assertTrue(thrown instanceof BadRequestException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(401)); });
        Assertions.assertTrue(thrown instanceof UnAuthorizedException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(403)); });
        Assertions.assertTrue(thrown instanceof ForbiddenException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(404)); });
        Assertions.assertTrue(thrown instanceof NotFoundException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(410)); });
        Assertions.assertTrue(thrown instanceof NotExistRegistIdException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(412)); });
        Assertions.assertTrue(thrown instanceof PreConditionFailedException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(422)); });
        Assertions.assertTrue(thrown instanceof PushEtcException);

    }


    @Test
    void testFeignException() {

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(202)); });
        Assertions.assertTrue(thrown instanceof AcceptedException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(205)); });
        Assertions.assertTrue(thrown instanceof PushEtcException);

    }

    Response getHttpResponse(int status) {
        return Response.builder()
                .status(status)
                .reason("-")
                .request(request)
                .headers(headers)
                .build();
    }
}
package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.data.mapper.PushMapper;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PushAnnounceClientImplTest {

    @InjectMocks
    private PushAnnounceClientImpl pushAnnounceClient;

    @Mock
    private PushAnnounceFeignClient pushAnnounceFeignClient;

    @Mock
    private PushConfig pushConfig;

    @Mock
    private PushMapper pushMapper;

    //private final ObjectMapper objectMapper = new ObjectMapper();

    Map<String, String> paramMap;
    String jsonNormal;

    @BeforeEach
    void setUp() {
        pushAnnounceClient = new PushAnnounceClientImpl(pushAnnounceFeignClient, pushConfig, pushMapper);

        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        paramMap = new HashMap<>();
        paramMap.put("msg_id", "PUSH_ANNOUNCEMENT");
        paramMap.put("push_id", DateFormatUtils.format(new Date(), "yyyyMMdd") + "0001");
        paramMap.put("service_id", "30011");
        paramMap.put("app_id", "lguplushdtvgcm");
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

        //test 1
        given( pushConfig.getCommPropValue("30011.announce.server.ip") ).willReturn(null);
        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willReturn(retMap);
        given( pushMapper.toResponseDto(anyMap()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

        PushResponseDto responseDto = pushAnnounceClient.requestAnnouncement(paramMap);
        Assertions.assertEquals("200", responseDto.getStatusCode());

        //test2
        given( pushConfig.getCommPropValue("30011.announce.server.ip") ).willReturn("192.168.1.1");
        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willReturn(retMap);
        given( pushMapper.toResponseDto(anyMap()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

        responseDto = pushAnnounceClient.requestAnnouncement(paramMap);
        Assertions.assertEquals("200", responseDto.getStatusCode());
    }

    //@Test
    void requestAnnouncement_ex1()  {
        FeignException ex = new FeignExceptionEx(("<" + jsonNormal).getBytes(StandardCharsets.UTF_8));
        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willThrow(ex);
        PushResponseDto responseDto = pushAnnounceClient.requestAnnouncement(paramMap);
        Assertions.assertEquals("5103", responseDto.getStatusCode());
    }

   // @Test
    void requestAnnouncement_ex2()  {
        FeignException ex = new FeignExceptionEx(jsonNormal.getBytes(StandardCharsets.UTF_8));
        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willThrow(ex);

        PushResponseDto mockDto = PushResponseDto.builder().statusCode("201").build();
        given(pushMapper.toResponseDto(anyMap())).willReturn(mockDto);

        PushResponseDto  responseDto = pushAnnounceClient.requestAnnouncement(paramMap);
        Assertions.assertEquals("201", responseDto.getStatusCode());
    }

    @Test
    void requestAnnouncement_ex3()  {
        Map<String, Collection<String>> headers = new HashMap<>();
        Request request = feign.Request.create(Request.HttpMethod.POST, "localhost:8080", headers, "---".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8, null);

        RetryableException ex = new RetryableExceptionEx(request);
        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willThrow(ex);

        Exception thrown = assertThrows(SocketTimeException.class, () -> {
            pushAnnounceClient.requestAnnouncement(paramMap);
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
        PushAnnounceClientImpl.PushErrorDecoder pushErrorDecoder = new PushAnnounceClientImpl.PushErrorDecoder();
        final Response response500 = getHttpResponse(500);
        assertThrows(InternalErrorException.class, () -> {throw pushErrorDecoder.decode("", response500); });
        final Response response502 = getHttpResponse(502);
        assertThrows(ExceptionOccursException.class, () -> {throw pushErrorDecoder.decode("", response502); });
        final Response response503 = getHttpResponse(503);
        assertThrows(ServiceUnavailableException.class, () -> {throw pushErrorDecoder.decode("", response503); });
        final Response response504 = getHttpResponse(504);
        assertThrows(PushEtcException.class, () -> {throw pushErrorDecoder.decode("", response504); });
    }

    @Test
    void testClientException() {
        PushAnnounceClientImpl.PushErrorDecoder pushErrorDecoder = new PushAnnounceClientImpl.PushErrorDecoder();
        final Response response400 = getHttpResponse(400);
        assertThrows(BadRequestException.class, () -> {throw pushErrorDecoder.decode("", response400); });
        final Response response401 = getHttpResponse(401);
        assertThrows(UnAuthorizedException.class, () -> {throw pushErrorDecoder.decode("", response401); });
        final Response response403 = getHttpResponse(403);
        assertThrows(ForbiddenException.class, () -> {throw pushErrorDecoder.decode("", response403); });
        final Response response404 = getHttpResponse(404);
        assertThrows(NotFoundException.class, () -> {throw pushErrorDecoder.decode("", response404); });
        final Response response410 = getHttpResponse(410);
        assertThrows(NotExistRegistIdException.class, () -> {throw pushErrorDecoder.decode("", response410); });
        final Response response412 = getHttpResponse(412);
        assertThrows(PreConditionFailedException.class, () -> {throw pushErrorDecoder.decode("", response412); });
        final Response response422 = getHttpResponse(422);
        assertThrows(PushEtcException.class, () -> {throw pushErrorDecoder.decode("", response422); });
    }

    @Test
    void testFeignException() {
        PushAnnounceClientImpl.PushErrorDecoder pushErrorDecoder = new PushAnnounceClientImpl.PushErrorDecoder();
        final Response response202 = getHttpResponse(202);
        assertThrows(AcceptedException.class, () -> {throw pushErrorDecoder.decode("", response202); });
        final Response response205 = getHttpResponse(205);
        assertThrows(PushEtcException.class, () -> {throw pushErrorDecoder.decode("", response205); });
    }

    Response getHttpResponse(int status) {

        Map<String, Collection<String>> headers = new LinkedHashMap<>();
        Request request = Request.create(Request.HttpMethod.POST, "/test", Collections.emptyMap(), null, Util.UTF_8, null);

        return Response.builder()
                .status(status)
                .reason("-")
                .request(request)
                .headers(headers)
                .build();
    }
}
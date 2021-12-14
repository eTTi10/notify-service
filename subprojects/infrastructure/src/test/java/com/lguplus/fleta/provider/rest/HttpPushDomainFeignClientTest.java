package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.ExclusionNumberException;
import feign.FeignException;
import feign.Response;
import fleta.util.JunitTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class HttpPushDomainFeignClientTest {

    private static final String SUCCESS_CODE = "200";

    @InjectMocks
    HttpPushDomainFeignClient httpPushDomainFeignClient;

    @Mock
    HttpPushFeignClient httpPushFeignClient;

    @BeforeEach
    void setUp() {
        JunitTestUtils.setValue(httpPushDomainFeignClient, "protocolSingle", "http");
    }

    @Test
    @DisplayName("정상적으로 단건푸시가 성공하는지 확인")
    void whenRequestSinglePush_thenReturnSuccess() {
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").build();

        // given
        given(httpPushFeignClient.requestHttpPushSingle(any(), anyMap(), anyMap())).willReturn(openApiPushResponseDto);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("app_id", "lguplushdtvgcm");
        paramMap.put("service_id", "30011");
        paramMap.put("push_type", "G");
        paramMap.put("users", List.of("01099991234"));
        paramMap.put("msg", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"");

        // when
        OpenApiPushResponseDto responseDto = httpPushDomainFeignClient.requestHttpPushSingle(paramMap);

        // then
        assertThat(responseDto.getReturnCode()).isEqualTo(SUCCESS_CODE);    // 성공 코드가 있는지 확인
    }

    @Test
    @DisplayName("Feign 호출시 예외처리 되는지 확인")
    void whenCallFeign_thenThrowFeignException() {
        URI uri = URI.create("http://211.115.75.227:5556");

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headerMap.put(HttpHeaders.ACCEPT_CHARSET, "utf-8");
        headerMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headerMap.put(HttpHeaders.CONTENT_ENCODING, "utf-8");
        headerMap.put(HttpHeaders.AUTHORIZATION, "auth=0000002640;C8ACEEC7A62254021B14");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("app_id", "lguplushdtvgcm");
        paramMap.put("service_id", "30011");
        paramMap.put("push_type", "G");
        paramMap.put("users", List.of("01099991234"));
        paramMap.put("msg", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"");

        when(httpPushFeignClient.requestHttpPushSingle(uri, headerMap, paramMap)).thenThrow(FeignException.errorStatus(
                "httpPushFeignClient",
                            Response.builder()
                                    .status(202)
                                    .headers(new HashMap<>())
                                    .reason("Not found").build()));

        Exception exception = assertThrows(FeignException.class, () -> {
            httpPushFeignClient.requestHttpPushSingle(uri, headerMap, paramMap);
        });

        assertThat(exception).isInstanceOf(FeignException.class);
    }
}
package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import fleta.util.JunitTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class HttpPushDomainFeignClientTest {

    private static final String SUCCESS_CODE = "200";

    @InjectMocks
    HttpPushDomainClientImpl httpPushDomainFeignClient;

    @Mock
    HttpPushFeignClient httpPushFeignClient;

    @BeforeEach
    void setUp() {
        JunitTestUtils.setValue(httpPushDomainFeignClient, "protocolSingle", "http");
        JunitTestUtils.setValue(httpPushDomainFeignClient, "protocolAnnounce", "http");
    }

    @Test
    @DisplayName("정상적으로 단건푸시가 성공하는지  확인")
    void whenRequestSinglePush_thenReturnSuccess() {
        // given
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").build();

        given(httpPushFeignClient.requestHttpPushSingle(any(), anyMap())).willReturn(openApiPushResponseDto);

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
    @DisplayName("정상적으로 공지푸시가 성공하는지 확인")
    void whenRequestAnnouncementPush_thenReturnSuccess() {
        // given
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").build();

        given(httpPushFeignClient.requestHttpPushAnnouncement(any(), anyMap())).willReturn(openApiPushResponseDto);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("app_id", "lguplushdtvgcm");
        paramMap.put("service_id", "30011");
        paramMap.put("push_type", "G");
        paramMap.put("msg", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"");
        paramMap.put("items", List.of("gcm_multi_count!^100"));

        // when
        OpenApiPushResponseDto responseDto = httpPushDomainFeignClient.requestHttpPushAnnouncement(paramMap);

        // then
        assertThat(responseDto.getReturnCode()).isEqualTo(SUCCESS_CODE);    // 성공 코드가 있는지 확인
    }

    @Test
    void testGetBaseUrl() throws Exception {
        HttpPushDomainClientImpl httpPushDomainClientImpl = new HttpPushDomainClientImpl(httpPushFeignClient);

        JunitTestUtils.setValue(httpPushDomainClientImpl, "hostSingle", "211.115.75.227");
        JunitTestUtils.setValue(httpPushDomainClientImpl, "protocolSingle", "http");
        JunitTestUtils.setValue(httpPushDomainClientImpl, "httpPortSingle", "5556");
        JunitTestUtils.setValue(httpPushDomainClientImpl, "httpsPortSingle", "6556");
        JunitTestUtils.setValue(httpPushDomainClientImpl, "hostAnnounce", "211.115.75.227");
        JunitTestUtils.setValue(httpPushDomainClientImpl, "protocolAnnounce", "http");
        JunitTestUtils.setValue(httpPushDomainClientImpl, "httpPortAnnounce", "5555");
        JunitTestUtils.setValue(httpPushDomainClientImpl, "httpsPortAnnounce", "6555");

        Method method = httpPushDomainClientImpl.getClass().getDeclaredMethod("getBaseUrl", String.class);
        method.setAccessible(true);

        String singlePushUrl = (String) method.invoke(httpPushDomainClientImpl, "S");
        assertThat(singlePushUrl).contains("5556");

        String announcementPushUrl = (String) method.invoke(httpPushDomainClientImpl, "A");
        assertThat(announcementPushUrl).contains("5555");

        JunitTestUtils.setValue(httpPushDomainClientImpl, "protocolSingle", "https");
        JunitTestUtils.setValue(httpPushDomainClientImpl, "protocolAnnounce", "https");

        singlePushUrl = (String) method.invoke(httpPushDomainClientImpl, "S");
        assertThat(singlePushUrl).contains("6556");

        announcementPushUrl = (String) method.invoke(httpPushDomainClientImpl, "A");
        assertThat(announcementPushUrl).contains("6555");
    }

}
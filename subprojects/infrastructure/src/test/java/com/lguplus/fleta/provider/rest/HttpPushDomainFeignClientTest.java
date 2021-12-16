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

}
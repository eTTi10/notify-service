package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.ExclusionNumberException;
import com.lguplus.fleta.util.HttpPushSupport;
import com.lguplus.fleta.util.JunitTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HttpPushDomainServiceTest {

    private static final String SUCCESS_CODE = "200";
    private static final String FAILURE_CODE = "401";

    @InjectMocks
    HttpPushDomainService httpPushDomainService;

    @Mock
    HttpPushDomainClient httpPushDomainClient;

    @Mock
    HttpPushSupport httpPushSupport;

    @BeforeEach
    void setUp() {
        JunitTestUtils.setValue(httpPushDomainService, "exception", "M20110725000|U01080800201|U01080800202|U01080800203");
    }

    @Test
    @DisplayName("정상적으로 단건푸시가 성공하는지 확인")
    void whenRequestSinglePush_thenReturnSuccess() {
        // given
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").build();

        given(httpPushDomainClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushSingleRequestDto httpPushSingleRequestDto = HttpPushSingleRequestDto.builder()
                .appId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234"))
                .msg("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        HttpPushResponseDto responseDto = httpPushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

    @Test
    @DisplayName("발송제외 가번 확인")
    void whenExclusionNumber_thenThrowExclusionNumberException() {
        HttpPushSingleRequestDto httpPushSingleRequestDto = HttpPushSingleRequestDto.builder()
                .appId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("M20110725000")) // 발송 제외 가번
                .msg("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        Exception exception = assertThrows(ExclusionNumberException.class, () -> {
            httpPushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);
        });

        assertThat(exception).isInstanceOf(ExclusionNumberException.class);    // ExclusionNumberException 이 발생하였는지 확인
    }

}
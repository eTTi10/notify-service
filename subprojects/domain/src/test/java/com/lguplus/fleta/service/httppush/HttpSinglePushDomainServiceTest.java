package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.properties.HttpServiceProps;
import com.lguplus.fleta.util.HttpPushSupport;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HttpSinglePushDomainServiceTest {

    private static final String SUCCESS_CODE = "200";

    @InjectMocks
    HttpSinglePushDomainService httpSinglePushDomainService;

    @Mock
    HttpPushClient httpPushClient;

    @Mock
    HttpPushSupport httpPushSupport;

    @Mock
    HttpServiceProps httpServiceProps;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(httpSinglePushDomainService, "rejectReg", Set.of("M20110725000","U01080800201","U01080800202","U01080800203"));
    }

    @Test
    @DisplayName("정상적으로 단건푸시가 성공하는지 확인")
    void whenRequestSinglePush_thenReturnSuccess() {
        // given
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushSingleRequestDto httpPushSingleRequestDto = HttpPushSingleRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234"))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        HttpPushResponseDto responseDto = httpSinglePushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

    @Test
    @DisplayName("발송제외 가번 확인")
    void whenExclusionNumber_thenThrowExclusionNumberException() {
        // given
        given(httpPushSupport.getHttpServiceProps()).willReturn(httpServiceProps);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("9998", "발송제한번호"));

        HttpPushSingleRequestDto httpPushSingleRequestDto = HttpPushSingleRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("M20110725000")) // 발송 제외 가번
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        Exception exception = assertThrows(HttpPushCustomException.class, () -> {
            httpSinglePushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(HttpPushCustomException.class);    // ExclusionNumberException 이 발생하였는지 확인
        assertThat(exception.getMessage()).isSameAs("발송제한번호");    // ExclusionNumberException 이 발생하였는지 확인
    }

}
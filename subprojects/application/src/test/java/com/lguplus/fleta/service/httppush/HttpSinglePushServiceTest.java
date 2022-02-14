package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HttpSinglePushServiceTest {

    private static final String SUCCESS_CODE = "200";

    @InjectMocks
    HttpSinglePushService httpSinglePushService;

    @Mock
    HttpSinglePushDomainService httpSinglePushDomainService;


    @Test
    @DisplayName("정상적으로 단건푸시가 성공하는지 확인")
    void whenRequestSinglePush_thenReturnSuccess() {
        // given
        HttpPushResponseDto httpPushResponseDto = HttpPushResponseDto.builder().build();

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        HttpPushSingleRequestDto requestDto = HttpPushSingleRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234"))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        HttpPushResponseDto responseDto = httpSinglePushService.requestHttpPushSingle(requestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

}
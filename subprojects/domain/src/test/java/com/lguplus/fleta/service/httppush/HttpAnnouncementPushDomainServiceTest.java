package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushAnnounceRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.util.HttpPushSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HttpAnnouncementPushDomainServiceTest {

    private static final String SUCCESS_CODE = "200";

    @InjectMocks
    HttpAnnouncementPushDomainService httpAnnouncementPushDomainService;

    @Mock
    HttpPushClient httpPushClient;

    @Mock
    HttpPushSupport httpPushSupport;

    @Test
    @DisplayName("정상적으로 공지푸시가 성공하는지 확인")
    void whenRequestAnnouncementPush_thenReturnSuccess() {
        // given
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").build();

        given(httpPushClient.requestHttpPushAnnouncement(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushAnnounceRequestDto httpPushAnnounceRequestDto = HttpPushAnnounceRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        HttpPushResponseDto responseDto = httpAnnouncementPushDomainService.requestHttpPushAnnouncement(httpPushAnnounceRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

}
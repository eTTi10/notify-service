package com.lguplus.fleta.service.uflix;

import com.lguplus.fleta.data.dto.request.outer.UxSimpleJoinSmsRequestDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.service.smsagent.SmsAgentDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UxSimpleJoinServiceTest {

    @InjectMocks
    UxSimpleJoinService uxSimpleJoinService;

    @Mock
    SmsAgentDomainService smsAgentDomainService;

    @Test
    @DisplayName("정상적으로 tvG 유플릭스 간편 가입 안내 SMS 요청이 성공하는지 확인")
    void whenRequestUxSimpleJoinSms_thenReturnSuccess() throws Exception {
        // given
        SmsGatewayResponseDto smsGatewayResponseDto = SmsGatewayResponseDto.builder().flag("0000").build();

        given(smsAgentDomainService.sendSms(any())).willReturn(smsGatewayResponseDto);

        UxSimpleJoinSmsRequestDto uxSimpleJoinSmsRequestDto = UxSimpleJoinSmsRequestDto.builder()
                .saId("500058151453")
                .stbMac("001c.627e.039c")
                .ctn("01055805424")
                .build();

        // when
        uxSimpleJoinService.requestUxSimpleJoinSms(uxSimpleJoinSmsRequestDto);

        // then
        verify(smsAgentDomainService).sendSms(any());
    }

}
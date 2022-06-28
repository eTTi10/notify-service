package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.service.mmsagent.MmsAgentDomainService;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MmsServiceTest {

    private static final String SUCCESS_CDE = "0000";

    @InjectMocks
    MmsService mmsService;

    @Mock
    MmsAgentDomainService mmsAgentDomainService;

    @BeforeEach
    void setUp() {

        mmsService = new MmsService(mmsAgentDomainService);

    }

    @Test
    @DisplayName("SMS 발송 성공 확인")
    void whenRequestSms_thenReturnSuccess() {
        SuccessResponseDto successResponseDto = SuccessResponseDto.builder().build();

        // given
        given(mmsAgentDomainService.sendMmsCode(any())).willReturn(successResponseDto);

        // mock object
        SendMmsRequestDto request = SendMmsRequestDto.builder()
            .saId("M15030600001")
            .stbMac("v150.3060.0001")
            .mmsCd("M011")
            .ctn("01051603997")
            .replacement("영희|컴퓨터")
            .build();

        // when
        SuccessResponseDto responseDto = mmsService.sendMms(request);

        // then
        assertThat(responseDto.getFlag()).isEqualTo(SUCCESS_CDE);
    }


}
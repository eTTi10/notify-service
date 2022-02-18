package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@DisplayName("REST로 문자내용을 제대로 가져오는 테스트")
class SmsCallSettingDomainFeignClientTest {

    @Mock
    SmsCallSettingFeignClient smsCallSettingFeignClient;

    @InjectMocks
    SmsCallSettingDomainFeignClient smsCallSettingDomainFeignClient;

    @Test
    void smsCallSettingApi() {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        CallSettingRequestDto callSettingRequestDto = CallSettingRequestDto.builder()
                .code("S001")
                .svcType("I")
                .build();

        //given
        given(smsCallSettingFeignClient.callSettingApi(any())).willReturn(resultMapDto);

        //when
        CallSettingResultMapDto callSettingResultMapDto = smsCallSettingDomainFeignClient.smsCallSettingApi(callSettingRequestDto);

        //then
        assertThat(callSettingResultMapDto.getCode().equals("S001"));

    }
}
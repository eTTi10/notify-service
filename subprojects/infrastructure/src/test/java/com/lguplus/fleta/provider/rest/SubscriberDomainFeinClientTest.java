package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class SubscriberDomainFeinClientTest {

    private static final String REG_ID = "M00020200205";

    @Mock
    SubscriberFeinClient subscriberFeinClient;

    @InjectMocks
    SubscriberDomainFeinClient subscriberDomainFeinClient;

    @Test
    @DisplayName("정상적으로 regId를 조회하는지 확인")
    void getRegistrationIDbyCtn() {

        //given
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();

        InnerResponseDto<RegIdDto> regIdDtoInnerResponseDto = new InnerResponseDto<>(InnerResponseCodeType.OK, regIdDto);

        given(subscriberFeinClient.getRegistrationIDbyCtn(any())).willReturn(regIdDtoInnerResponseDto);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("ctn", "M00020200205");

        //when
        RegIdDto responseDto = subscriberDomainFeinClient.getRegistrationIDbyCtn(paramMap);

        //then
        assertThat(responseDto.getRegistrationId().equals(REG_ID));

    }
}
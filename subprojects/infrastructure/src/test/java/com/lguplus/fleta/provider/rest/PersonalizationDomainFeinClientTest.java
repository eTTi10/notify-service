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
class PersonalizationDomainFeinClientTest {

    private static final String REG_ID = "0kjk7HCvk64QtUd+3Gfw0cdiE53ID/5XstD2so0MCgk=";

    @Mock
    PersonalizationFeinClient personalizationFeinClient;

    @InjectMocks
    PersonalizationDomainFeinClient personalizationDomainFeinClient;

    @Test
    @DisplayName("정상적으로 regId를 조회하는지 확인")
    void getRegistrationID() {

        //given
        RegIdDto regIdDto = RegIdDto.builder()
                .registrationId(REG_ID)
                .build();

        InnerResponseDto<RegIdDto> regIdDtoInnerResponseDto = new InnerResponseDto<>(InnerResponseCodeType.OK, regIdDto);
        given(personalizationFeinClient.getRegistrationID(any())).willReturn(regIdDtoInnerResponseDto);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("saId", "500223046118");
        paramMap.put("stbMac", "f83b.1d31.6c36");

        //when
        RegIdDto responseDto = personalizationDomainFeinClient.getRegistrationID(paramMap);

        //then
        assertThat(responseDto.getRegistrationId()).isEqualTo(REG_ID);

    }
}
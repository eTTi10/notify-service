package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.SaIdDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
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
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class SubscriberDomainFeignClientTest {

    private static final String SA_ID = "500223046118";

    @Mock
    SubscriberFeignClient subscriberFeignClient;

    @InjectMocks
    SubscriberDomainFeignClient subscriberDomainFeignClient;

    @Test
    @DisplayName("정상적으로 regId를 조회하는지 확인")
    void getRegistrationIDbyCtn() {

        //given

        SaIdDto saIdDto = SaIdDto.builder().saId(SA_ID).build();
        List<SaIdDto> saIdDtos = List.of(saIdDto);

        InnerResponseDto<List<SaIdDto>> regIdDtoInnerResponseDto = new InnerResponseDto<List<SaIdDto>>(InnerResponseCodeType.OK, saIdDtos);

        given(subscriberFeignClient.getRegistrationIDbyCtn(any())).willReturn(regIdDtoInnerResponseDto);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("ctnNo", "001039752719");

        //when
        List<SaIdDto> responseDto = subscriberDomainFeignClient.getRegistrationIDbyCtn(paramMap);

        //then
        assertThat(responseDto.get(0).getSaId()).isEqualTo(SA_ID);

    }
}
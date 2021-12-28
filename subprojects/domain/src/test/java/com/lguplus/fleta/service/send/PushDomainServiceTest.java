package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.service.httppush.HttpPushDomainService;
import com.lguplus.fleta.util.JunitTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PushDomainServiceTest {

    private static final String REG_ID = "M00020200205";

    @InjectMocks
    PushDomainService pushDomainService;

    @Mock
    PersonalizationDomainClient personalizationDomainClient;

    @Mock
    SubscriberDomainClient subscriberDomainClient;

    @BeforeEach
    void setUp() {
        JunitTestUtils.setValue(pushDomainService, "extraServiceId", "30015");
        JunitTestUtils.setValue(pushDomainService, "extraAppId", "smartuxapp");
    }

    @Test
    @DisplayName("정상적으로 regId를 가져오는 지 확인")
    void getRegistrationID() {
        //given
        RegIdDto regIdDto = RegIdDto.builder().regId(REG_ID).build();

        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .saId("500058151453")
                .stbMac("001c.627e.039c")
                .build();

        //when
        String regId = pushDomainService.getRegistrationID(sendPushCodeRequestDto);

        //then
        assertThat(regId.equals(REG_ID));
    }

    @Test
    @DisplayName("ctn을 입력으로 받아 정상적으로 regId 조회하는 지 확인")
    void getRegistrationIDbyCtn() {

        //given
        RegIdDto regIdDto = RegIdDto.builder().regId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);
        String ctn = REG_ID;

        //when
        String regId = pushDomainService.getRegistrationIDbyCtn(ctn);

        //then
        assertThat(regId.equals(REG_ID));
    }

    @Test
    void getGcmOrTVRequestDto() {
    }

    @Test
    void getApnsRequestDto() {
    }

    @Test
    void getPosRequestDto() {
    }

    @Test
    void getExtraPushRequestDto() {
    }
}
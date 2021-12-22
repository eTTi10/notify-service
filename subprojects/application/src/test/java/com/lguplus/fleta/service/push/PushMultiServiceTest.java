package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PushMultiServiceTest {


    @InjectMocks
    PushMultiService pushMultiService;

    @Mock
    PushMultiDomainService pushMultiDomainService;

    @BeforeEach
    void setUp() {
        pushMultiService = new PushMultiService(pushMultiDomainService);
    }

    @Test
    void requestMultiPush() {

        PushClientResponseDto clientResponseDto = PushClientResponseDto.builder().build();
        given(pushMultiDomainService.requestMultiPush(any())).willReturn(clientResponseDto);

        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");


        PushRequestMultiDto pushRequestMultiDto = PushRequestMultiDto.builder()
                .serviceId("lguplushdtvgcm")
                .pushType("G")
                .appId("30011")
                .users(items)
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();

        PushClientResponseDto responseDto = pushMultiService.requestMultiPush(pushRequestMultiDto);

        Assertions.assertEquals("200", responseDto.getCode());
    }
}
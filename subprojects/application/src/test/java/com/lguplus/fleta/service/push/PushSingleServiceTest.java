package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
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
class PushSingleServiceTest {


    @InjectMocks
    PushSingleService pushSingleService;

    @Mock
    PushSingleDomainService pushSingleDomainService;

    @BeforeEach
    void setUp() {
        pushSingleService = new PushSingleService(pushSingleDomainService);
    }

    @Test
    void requestPushSingle() {

        PushClientResponseDto clientResponseDto = PushClientResponseDto.builder().build();
        given(pushSingleDomainService.requestPushSingle(any())).willReturn(clientResponseDto);

        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        PushRequestSingleDto pushRequestSingleDto = PushRequestSingleDto.builder()
                .serviceId("lguplushdtvgcm")
                .pushType("G")
                .appId("30011")
                .regId("regId")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();

        PushClientResponseDto responseDto = pushSingleService.requestPushSingle(pushRequestSingleDto);

        Assertions.assertTrue("200".equals(responseDto.getCode()));
    }
}
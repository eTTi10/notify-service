package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseMultiDto;
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

        PushClientResponseMultiDto clientResponseDto = PushClientResponseMultiDto.builder().build();
        given(pushMultiDomainService.requestMultiPush(any())).willReturn(clientResponseDto);

        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        List<PushRequestItemDto> addItems = new ArrayList<>();
        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        PushRequestMultiDto pushRequestMultiDto = PushRequestMultiDto.builder()
                .serviceId("lguplushdtvgcm")
                .pushType("G")
                .appId("30011")
                .users(items)
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();

        PushClientResponseMultiDto responseDto = pushMultiService.requestMultiPush(pushRequestMultiDto);

        Assertions.assertEquals("200", responseDto.getCode());
    }
}
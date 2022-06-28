package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        List<PushRequestItemDto> addItems = new ArrayList<>();
        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        PushRequestSingleDto pushRequestSingleDto = PushRequestSingleDto.builder()
            .serviceId("lguplushdtvgcm")
            .pushType("G")
            .applicationId("30011")
            .regId("regId")
            .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
            .items(addItems)
            .build();

        PushClientResponseDto responseDto = pushSingleService.requestPushSingle(pushRequestSingleDto);

        Assertions.assertEquals("200", responseDto.getCode());
    }
}
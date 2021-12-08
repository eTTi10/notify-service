package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PushAnnouncementServiceTest {

    @InjectMocks
    PushAnnouncementService pushAnnouncementService;

    @Mock
    PushAnnounceDomainService pushAnnounceDomainService;

    @BeforeEach
    void setUp() {
        pushAnnouncementService = new PushAnnouncementService(pushAnnounceDomainService);
    }

    @Test
    void requestAnnouncement() {

        PushClientResponseDto clientResponseDto = PushClientResponseDto.builder().build();
        given(pushAnnouncementService.requestAnnouncement(any())).willReturn(clientResponseDto);

        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        PushRequestAnnounceDto pushRequestAnnounceDto = PushRequestAnnounceDto.builder()
                .serviceId("lguplushdtvgcm")
                .pushType("G")
                .appId("30011")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();

        PushClientResponseDto responseDto = pushAnnouncementService.requestAnnouncement(pushRequestAnnounceDto);

        Assertions.assertTrue("200".equals(responseDto.getCode()));

    }
}
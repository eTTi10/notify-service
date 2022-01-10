package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushAnnounceDomainClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class PushAnnounceDomainServiceTest {

    @InjectMocks
    private PushAnnounceDomainService pushAnnounceDomainService;

    @Mock
    private PushConfig pushConfig;

    @Mock
    private PushAnnounceDomainClient pushAnnounceDomainClient;

    private PushRequestAnnounceDto  pushRequestAnnounceDto;

    @BeforeEach
    void setUp() {
        pushAnnounceDomainService = new PushAnnounceDomainService(pushConfig, pushAnnounceDomainClient);

        List<PushRequestItemDto> addItems = new ArrayList<>();
        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        pushRequestAnnounceDto = PushRequestAnnounceDto.builder()
                .serviceId("lguplushdtvgcm")
                .pushType("G")
                .applicationId("30011")
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();
    }

    @Test
    void requestAnnouncement() {
        //normal case
        given( pushConfig.getServicePassword(anyString()) ).willReturn("--password--");
        given( pushAnnounceDomainClient.requestAnnouncement(anyMap()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

        PushClientResponseDto responseDto = pushAnnounceDomainService.requestAnnouncement(pushRequestAnnounceDto);
        Assertions.assertEquals("200", responseDto.getCode());
    }

    @Test
    void requestAnnouncement_password_null() {
        //servicePwd null case
        given( pushConfig.getServicePassword(anyString()) ).willReturn(null);

        assertThrows(ServiceIdNotFoundException.class, () -> {
            pushAnnounceDomainService.requestAnnouncement(pushRequestAnnounceDto);
        });
    }

    @Test
    void requestAnnouncement_LGUPUSH_OLD() {
        //normal case lgpush
        given( pushConfig.getServicePassword(anyString()) ).willReturn("--password--");
        given( pushConfig.getServiceLinkType(anyString()) ).willReturn("LGUPUSH_OLD");
        given( pushAnnounceDomainClient.requestAnnouncement(anyMap()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

        ReflectionTestUtils.setField(pushAnnounceDomainService, "tranactionMsgId", new AtomicInteger(9999));

        PushClientResponseDto responseDto = pushAnnounceDomainService.requestAnnouncement(pushRequestAnnounceDto);
        Assertions.assertEquals("200", responseDto.getCode());
    }

   // @Test
    void requestAnnouncement_etc_return() {
        //servicePwd null case
        given( pushConfig.getServicePassword(anyString()) ).willReturn("--password--");

        List<String> codeList = Arrays.asList(new String[]{"202", "400", "401","403", "404", "410","412", "500", "502","503", "5102"});//, "-"});

        int count = 0;
        for(String code : codeList) {
            given( pushAnnounceDomainClient.requestAnnouncement(anyMap()) ).willReturn(PushResponseDto.builder().statusCode(code).build());

            assertThrows(NotifyRuntimeException.class, () -> {
                pushAnnounceDomainService.requestAnnouncement(pushRequestAnnounceDto);
            });
        }
    }

}
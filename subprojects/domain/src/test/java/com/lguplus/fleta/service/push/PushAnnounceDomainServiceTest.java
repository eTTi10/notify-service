package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushAnnounceDomainClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        pushRequestAnnounceDto = PushRequestAnnounceDto.builder()
                .serviceId("lguplushdtvgcm")
                .pushType("G")
                .appId("30011")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
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
//        given( pushAnnounceDomainClient.requestAnnouncement(anyMap()) ).willReturn(new PushAnnounceResponseDto("200", ""));

        Exception thrown = assertThrows(ServiceIdNotFoundException.class, () -> {
            PushClientResponseDto responseDto = pushAnnounceDomainService.requestAnnouncement(pushRequestAnnounceDto);
        });

        Assertions.assertTrue(thrown instanceof ServiceIdNotFoundException);
    }

    @Test
    void requestAnnouncement_LGUPUSH_OLD() {
        //normal case lgpush
        given( pushConfig.getServicePassword(anyString()) ).willReturn("--password--");
        given( pushConfig.getServiceLinkType(anyString()) ).willReturn("LGUPUSH_OLD");
        given( pushAnnounceDomainClient.requestAnnouncement(anyMap()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

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

            Exception thrown = assertThrows(NotifyPushRuntimeException.class, () -> {
                PushClientResponseDto responseDto = pushAnnounceDomainService.requestAnnouncement(pushRequestAnnounceDto);
            });

            boolean isNotiPush = thrown instanceof NotifyPushRuntimeException;
            if(isNotiPush)
                count ++;
        }
        assertEquals(count, codeList.size());
    }

   // @Test
    void requestAnnouncement_runtime_exception() {
        //servicePwd null case
        given( pushConfig.getServicePassword(anyString()) ).willReturn("--password--");

        List<String> codeList = Arrays.asList(new String[]{"-"});

        int count = 0;
        for(String code : codeList) {
            given( pushAnnounceDomainClient.requestAnnouncement(anyMap()) ).willReturn(PushResponseDto.builder().statusCode(code).build());

            Exception thrown = assertThrows(RuntimeException.class, () -> {
                PushClientResponseDto responseDto = pushAnnounceDomainService.requestAnnouncement(pushRequestAnnounceDto);
            });

            boolean isNotiPush = thrown instanceof RuntimeException;
            if(isNotiPush)
                count ++;
        }
        assertEquals(count, codeList.size());
    }

    /*
    when(personApiService.getPersonListByFirstChar('c'))
        .thenReturn(Collections.singletonList(Person.builder().name("corn").citySeq(4L).build()));
    personService.savePerson('c');
     */
}
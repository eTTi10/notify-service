package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException;
import com.lguplus.fleta.properties.SendPushCodeProps;
import com.lguplus.fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PushDomainServiceTest {

    private static final String REG_ID = "M00020200205";

    HttpPushSingleRequestDto httpPushSingleRequestDto;
    SendPushCodeRequestDto sendPushCodeRequestDto;
    Map<String, String> serviceTargetDefaultMap = new HashMap<>();
    Map<String, String> serviceTargetMap = new HashMap<>();
    Map<String, String> sendCodeMap = new HashMap<>();
    List<String> items = new ArrayList<>();

    @InjectMocks
    PushDomainService pushDomainService;

    @Mock
    SendPushCodeProps sendPushCodeProps;

    @Mock
    PersonalizationDomainClient personalizationDomainClient;

    @Mock
    SubscriberDomainClient subscriberDomainClient;

    @BeforeEach
    void setUp() {
        JunitTestUtils.setValue(pushDomainService, "extraServiceId", "30015");
        JunitTestUtils.setValue(pushDomainService, "extraAppId", "smartuxapp");


        //response given
        httpPushSingleRequestDto = HttpPushSingleRequestDto.builder()
                .applicationId("musicshow_gcm")
                .serviceId("30104")
                .pushType("G")
                .message("\"result\":{\"noti_type\":\"PA_TM\", \"address\":\"111111\", \"unumber\":\"948-0719\",\"req_date\":\"202002141124\",\"ctn\":\"\",\"trans_id\":\"\"}")
                .users(List.of("M00020200205"))
                .items(List.of("badge!^1", "sound!^ring.caf", "cm!^aaaa"))
                .build();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        // 기본 입력
        Map<String, String> reserveMap = Map.of("address","111111");

        sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .regId("M00020200205")
                .pushType("G")
                .sendCode("P001")
                .regType("1")
                .serviceType("C")
                .reserve(reserveMap)
                .items(items)
                .build();

        serviceTargetDefaultMap.put("gcm.appid", "hdtv_GCM01");
        serviceTargetDefaultMap.put("gcm.serviceid", "20014");
        serviceTargetDefaultMap.put("apns.appid", "hdtv_APNS1");
        serviceTargetDefaultMap.put("apns.serviceid", "30005");

        serviceTargetMap.put("gcm.appid", "musicshow_gcm");
        serviceTargetMap.put("gcm.serviceid", "30104");
        serviceTargetMap.put("apns.appid", "musicshow_apns");
        serviceTargetMap.put("apns.serviceid", "30107");
        serviceTargetMap.put("pos.appid", "smartuxapp");
        serviceTargetMap.put("pos.serviceid", "30015");

        sendCodeMap.put("gcm.payload.body", "\"result\":{\"noti_type\":\"PA_TM\", \"address\":\"[+address]\", \"unumber\":\"[+unumber]\",\"req_date\":\"[+req_date]\",\"ctn\":\"[+ctn]\",\"trans_id\":\"[+trans_id]\"}");
        sendCodeMap.put("apns.payload.body", "\"body\":\"U+tv에서 연결을 요청했습니다\"");
        sendCodeMap.put("apns.payload.item", "cm!^PA_TM|[+address]|[+unumber]|[+req_date]|[+ctn]|[+trans_id]");
        sendCodeMap.put("param.list", "address|unumber|req_date|ctn|trans_id");
        sendCodeMap.put("pos.send", "Y");


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
        given(subscriberDomainClient.getRegistrationIDbyCtn(any())).willReturn(regIdDto);
        String ctn = REG_ID;

        //when
        String regId = pushDomainService.getRegistrationIDbyCtn(ctn);

        //then
        assertThat(regId.equals(REG_ID));
    }

    @Test
    void getGcmOrTVRequestDto() {

        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMap));
        RegIdDto regIdDto = RegIdDto.builder().regId(REG_ID).build();
        given(subscriberDomainClient.getRegistrationIDbyCtn(any())).willReturn(regIdDto);

        //serviceType : C
        HttpPushSingleRequestDto result;
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "C");
        assertThat(result.equals(httpPushSingleRequestDto));

        //serviceType : H
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "H");
        assertThat(result.equals(httpPushSingleRequestDto));

        //serviceType : ""
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "");
        assertThat(result.equals(httpPushSingleRequestDto));

        //pushType : "A"
        sendPushCodeRequestDto.setPushType("A");
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "");
        assertThat(result.equals(httpPushSingleRequestDto));

        //regType : 2
        sendPushCodeRequestDto.setRegType("2");
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "");
        assertThat(result.equals(httpPushSingleRequestDto));

    }

    @Test
    void checkInvalidSendPushCode() {

        //given
        Map<String, String> pushInfoMap = new HashMap<>();
        pushInfoMap.put("gcm.payload.body", "tesst");
        pushDomainService.checkInvalidSendPushCode(pushInfoMap);

        Map<String, String> pushInfoMapE = new HashMap<>();
        pushInfoMapE.put("gcm.payload.body", "");
        //sendcode 미지원 exceptioin
        Exception exception = assertThrows(InvalidSendPushCodeException.class, () -> {
            pushDomainService.checkInvalidSendPushCode(pushInfoMapE);
        });
        assertThat(exception.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException");

    }

    @Test
    void getApnsRequestDto() {

        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMap));
        RegIdDto regIdDto = RegIdDto.builder().regId(REG_ID).build();
        given(subscriberDomainClient.getRegistrationIDbyCtn(any())).willReturn(regIdDto);

        HttpPushSingleRequestDto result;
        //pushType : "A"
        sendPushCodeRequestDto.setPushType("A");
        result = pushDomainService.getApnsRequestDto(sendPushCodeRequestDto, "C");
        assertThat(result.equals(httpPushSingleRequestDto));

        //regType : 2
        sendPushCodeRequestDto.setRegType("2");
        result = pushDomainService.getApnsRequestDto(sendPushCodeRequestDto, "C");
        assertThat(result.equals(httpPushSingleRequestDto));

        //serviceType : H
        result = pushDomainService.getApnsRequestDto(sendPushCodeRequestDto, "H");
        assertThat(result.equals(httpPushSingleRequestDto));

        //serviceType : ""
        result = pushDomainService.getApnsRequestDto(sendPushCodeRequestDto, "");
        assertThat(result.equals(httpPushSingleRequestDto));


    }

    @Test
    void getPosRequestDto() {

        //정상
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMap));
        RegIdDto regIdDto = RegIdDto.builder().regId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        HttpPushSingleRequestDto result;
        sendPushCodeRequestDto.setPushType("L");
        result = pushDomainService.getPosRequestDto(sendPushCodeRequestDto, "C");
        assertThat(result.equals(httpPushSingleRequestDto));


        //LG Push 미지원 1
        Map<String, String> serviceTargetMapE = new HashMap<>();
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMapE));
        Exception exception = assertThrows(InvalidSendPushCodeException.class, () -> {
            pushDomainService.getPosRequestDto(sendPushCodeRequestDto, "C");
        });
        assertThat(exception.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException");

        //LG Push 미지원 2
        serviceTargetMapE.put("pos.serviceid","");
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMapE));
        Exception exception2 = assertThrows(InvalidSendPushCodeException.class, () -> {
            pushDomainService.getPosRequestDto(sendPushCodeRequestDto, "C");
        });
        assertThat(exception2.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException");


    }

    @Test
    void getExtraPushRequestDto() {

        //given
        PushRequestSingleDto pushRequestSingleDto = PushRequestSingleDto.builder()
                .appId("30015")
                .serviceId("smartuxapp")
                .pushType("L")
                .msg("\"result\":{\"noti_type\":\"PA_TM\", \"address\":\"111111\", \"unumber\":\"948-0719\",\"req_date\":\"202002141124\",\"ctn\":\"\",\"trans_id\":\"\"}")
                .regId("M00020200205")
                .items(items)
                .build();

        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMap));
        RegIdDto regIdDto = RegIdDto.builder().regId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        //serviceType : C
        PushRequestSingleDto result;
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "C");
        assertThat(result.equals(pushRequestSingleDto));

        //serviceType : H
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "H");
        assertThat(result.equals(pushRequestSingleDto));

        //serviceType : ""
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "");
        assertThat(result.equals(pushRequestSingleDto));

        //pushType : "A"
        sendPushCodeRequestDto.setPushType("A");
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "");
        assertThat(result.equals(pushRequestSingleDto));

        //regType : 2
        sendPushCodeRequestDto.setRegType("2");
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "");
        assertThat(result.equals(pushRequestSingleDto));

    }
}
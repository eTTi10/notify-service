package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.SaIdDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.PushServiceResultDto;
import com.lguplus.fleta.data.dto.response.SendPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException;
import com.lguplus.fleta.exception.push.PushEtcException;
import com.lguplus.fleta.properties.SendPushCodeProps;
import com.lguplus.fleta.service.httppush.HttpSinglePushDomainService;
import com.lguplus.fleta.service.push.PushSingleDomainService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PushSenderTest {

    private static final String REG_ID = "M00020200205";
    private static final String SA_ID = "500223046118";

    HttpPushSingleRequestDto httpPushSingleRequestDto;
    SendPushCodeRequestDto sendPushCodeRequestDto;

    HttpPushResponseDto httpPushResponseDto;

    Map<String, Map<String, String>> serviceTargetMap = new HashMap<>();
    Map<String, Map<String, String>> sendCodeMap = new HashMap<>();
    List<String> items = new ArrayList<>();

    String sFlag = "0000";
    String sMessage = "성공";

    @InjectMocks
    PushSender pushSender;

    @Mock
    HttpSinglePushDomainService httpSinglePushDomainService;

    @Mock
    PushSingleDomainService pushSingleDomainService;

    @Mock
    SendPushCodeProps sendPushCodeProps;

    @Mock
    PersonalizationDomainClient personalizationDomainClient;

    @Mock
    SubscriberDomainClient subscriberDomainClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pushSender, "fcmExtraSend", "N");
        ReflectionTestUtils.setField(pushSender, "extraServiceId", "30015");
        ReflectionTestUtils.setField(pushSender, "extraApplicationId", "smartuxapp");

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
        Map<String, String> reserveMap = Map.of("address", "111111",
            "unumber", "948-0719",
            "req_date", "202002141124");

        sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G")
            .sendCode("P001")
            .regType("1")
            .serviceType("C")
            .reserve(reserveMap)
            .items(items)
            .build();

        //response given
        httpPushResponseDto = HttpPushResponseDto.builder()
            .code(sFlag)
            .message(sMessage)
            .build();

        serviceTargetMap = Map.of(
            "default", Map.of(
                "gcm.appid", "hdtv_GCM01",
                "gcm.serviceid", "20014",
                "apns.appid", "hdtv_APNS1",
                "apns.serviceid", "30005"),
            "B", Map.of(
                "gcm.appid", "lguplus_base_fcm",
                "gcm.serviceid", "30133",
                "apns.appid", "lguplus_base_apns",
                "apns.serviceid", "30134"),
            "K", Map.of(
                "gcm.appid", "lguplus_mobilekids_fcm",
                "gcm.serviceid", "30135",
                "apns.appid", "lguplus_mobilekids_apns",
                "apns.serviceid", "30136"),
            "O", Map.of(
                "gcm.appid", "ugolf_GCM",
                "gcm.serviceid", "30105",
                "apns.appid", "ugolf_APNS",
                "apns.serviceid", "30106"),
            "C", Map.of(
                "gcm.appid", "musicshow_gcm",
                "gcm.serviceid", "30104",
                "apns.appid", "musicshow_apns",
                "apns.serviceid", "30107",
                "pos.appid", "smartuxapp",
                "pos.serviceid", "30015"),
            "TV", Map.of(
                "gcm.appid", "smart_uxapp_fcm",
                "gcm.serviceid", "30128",
                "apns.appid", "smart_uxapp_fcm",
                "apns.serviceid", "30128"));

        sendCodeMap = Map.of(
            "P001", Map.of(
                "gcm.payload.body", "\"result\":{\"noti_type\":\"PA_TM\", \"address\":\"[+address]\", \"unumber\":\"[+unumber]\",\"req_date\":\"[+req_date]\",\"ctn\":\"[+ctn]\",\"trans_id\":\"[+trans_id]\"}",
                "apns.payload.body", "\"body\":\"U+tv에서 연결을 요청했습니다\"",
                "apns.payload.item", "cm!^PA_TM|[+address]|[+unumber]|[+req_date]|[+ctn]|[+trans_id]",
                "param.list", "address|unumber|req_date|ctn|trans_id",
                "pos.send", "Y"),
            "P002", Map.of(
                "gcm.payload.body", "\"result\":{\"noti_type\":\"MFUP\", \"cont_type\":\"VOD\", \"album_id\":\"[+album_id]\", \"link_flag\":\"[+link_flag]\", \"name\":\"[+name]\", \"reqtime\":\"[+reqtime]\", \"playtime\":\"[+playtime]\", \"intent\":\"[+intent_url]\"}",
                "apns.payload.body", "\"body\":\"U+tv에서 보던 영상을 휴대폰으로 이어보세요\"",
                "apns.payload.item", "cm!^MFUP|VOD|[+album_id]|[+link_flag]|[+name]|[+reqtime]|[+playtime]|[+intent_url]",
                "param.list", "album_id|link_flag|name|reqtime|playtime|intent_url",
                "pos.send", "N"),
            "P003", Map.of(
                "gcm.payload.body", "\"result\":{\"noti_type\":\"MFUP\", \"cont_type\":\"REAL\", \"svc_id\":\"[+svc_id]\", \"name\":\"[+name]\", \"intent\":\"[+intent_url]\"}",
                "apns.payload.body", "\"body\":\"U+tv에서 보던 실시간 채널을 휴대폰으로 이어보세요\"",
                "apns.payload.item", "cm!^MFUP|REAL|[+svc_id]|[+name]|[+intent_url]",
                "param.list", "svc_id|name|intent_url",
                "pos.send", ""),
            "C001", Map.of(
                "gcm.payload.body", "\"result\":{\"noti_type\":\"PA_COMPL\",\"service_type\":\"[+service_type]\"}",
                "apns.payload.body", "\"body\":\"U+tv와 연결했습니다\"",
                "apns.payload.item", "cm!^PA_COMPL|[+service_type]",
                "param.list", "service_type|",
                "pos.send", ""),
            "C002", Map.of(
                "gcm.payload.body", "\"result\":{\"noti_type\":\"PA_CLEAR\",\"service_type\":\"[+service_type]\"}",
                "apns.payload.body", "\"body\":\"U+tv와 연결을 해제했습니다\"",
                "apns.payload.item", "cm!^PA_CLEAR|[+service_type]",
                "param.list", "service_type|",
                "pos.send", ""),
            "C003", Map.of(
                "gcm.payload.body", "\"result\":{\"noti_type\":\"PA_CANCEL\",\"service_type\":\"[+service_type]\"}",
                "apns.payload.body", "",
                "apns.payload.item", "",
                "param.list", "service_type|",
                "pos.send", "N"),
            "T001", Map.of(
                "gcm.payload.body", "\"result\":{\"noti_type\":\"PAIR\",\"service_type\":\"[+service_type]\",\"ctn\":\"[+ctn]\",\"pin\":\"[+pin]\"}",
                "apns.payload.body", "",
                "apns.payload.item", "",
                "param.list", "service_type|ctn|pin",
                "pos.send", ""),
            "T002", Map.of(
                "gcm.payload.body",
                "\"result\":{\"noti_type\":\"FUP\",\"cont_type\":\"VOD\",\"album_id\":\"[+album_id]\",\"album_series\":\"[+album_series]\",\"cate_series\":\"[+cate_series]\",\"name\":\"[+name]\",\"duration\":\"[+duration]\",\"playtime\":\"[+playtime]\",\"service_type\":\"[+service_type]\",\"ctn\":\"[+ctn]\",\"data\":{\"LINK_FLAG\":\"[+link_flag]\", \"intent_url\":\"[+intent_url]\"}}",
                "apns.payload.body", "",
                "apns.payload.item", "",
                "param.list", "album_id|album_series|cate_series|name|duration|playtime|service_type|ctn|intent_url|link_flag",
                "pos.send", ""),
            "T003", Map.of(
                "gcm.payload.body", "\"result\":{\"noti_type\":\"FUP\",\"cont_type\":\"REAL\",\"svc_id\":\"[+svc_id]\",\"name\":\"[+name]\",\"service_type\":\"[+service_type]\",\"ctn\":\"[+ctn]\",\"data\":{\"LINK_FLAG\":\"[+link_flag]\", \"intent_url\":\"[+intent_url]\"}}",
                "apns.payload.body", "",
                "apns.payload.item", "",
                "param.list", "svc_id|name|service_type|ctn|intent_url|link_flag",
                ", pos.send", ""));
    }


    @Test
    @DisplayName("정상적인 응답을 하는지 테스트")
    void sendPushCode() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(serviceTargetMap.get("TV"))).when(sendPushCodeProps).findMapByServiceType("TV");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");
        doReturn(Optional.of(sendCodeMap.get("T001"))).when(sendPushCodeProps).findMapBySendCode("T001");

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
            .type(sFlag)
            .message(sMessage)
            .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
            .message(sMessage)
            .service(pushServiceResultDtoArrayList)
            .build();

        List<String> items = new ArrayList<String>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G|A|L")
            .sendCode("P001")
            .regType("1")
            .serviceType("C|TV")
            .reserve(reserveMap)
            .items(items)
            .build();

        SendPushResponseDto responseDto;

        //정상 응답
        responseDto = pushSender.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage()).isEqualTo(sendPushResponseDto.getMessage());

        // (sendCode.substring(0,1).equals("T")) 테스트
        sendPushCodeRequestDto.setSendCode("T001");
        responseDto = pushSender.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage()).isEqualTo(sendPushResponseDto.getMessage());

    }

    @Test
    void sendPushCode_TooManyItemsThenSkip() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(serviceTargetMap.get("TV"))).when(sendPushCodeProps).findMapByServiceType("TV");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
            .type(sFlag)
            .message(sMessage)
            .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
            .message(sMessage)
            .service(pushServiceResultDtoArrayList)
            .build();

        List<String> items = new ArrayList<String>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa!^ddd");

        Map<String, String> reserveMap = Map.of("address", "111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G|A|L")
            .sendCode("P001")
            .regType("1")
            .serviceType("C|TV")
            .reserve(reserveMap)
            .items(items)
            .build();

        SendPushResponseDto responseDto;

        //정상 응답
        responseDto = pushSender.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage()).isEqualTo(sendPushResponseDto.getMessage());
    }

    @Test
    @DisplayName("응답메시지가 성공이 아닐 때 테스트")
    void returnFailMessage() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(serviceTargetMap.get("TV"))).when(sendPushCodeProps).findMapByServiceType("TV");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        httpPushResponseDto = HttpPushResponseDto.builder()
            .code(sFlag)
            .message("실패")
            .build();

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willThrow(new HttpPushCustomException(null, "202", "실패"));

        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
            .type(sFlag)
            .message("실패")
            .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
            .message("실패")
            .service(pushServiceResultDtoArrayList)
            .build();

        List<String> items = new ArrayList<>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        final SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G|A|L")
            .sendCode("P001")
            .regType("1")
            .serviceType("C|TV")
            .reserve(reserveMap)
            .items(items)
            .build();

        SendPushResponseDto responseDto;

        //정상 응답
        responseDto = pushSender.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage()).isEqualTo(sendPushResponseDto.getMessage());
    }

    @Test
    void returnFailMessage2() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(serviceTargetMap.get("TV"))).when(sendPushCodeProps).findMapByServiceType("TV");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        httpPushResponseDto = HttpPushResponseDto.builder()
            .code(sFlag)
            .message("실패")
            .build();

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
            .type(sFlag)
            .message("실패")
            .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
            .message("실패")
            .service(pushServiceResultDtoArrayList)
            .build();

        List<String> items = new ArrayList<>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        final SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G|A|L")
            .sendCode("P001")
            .regType("1")
            .serviceType("C|TV")
            .reserve(reserveMap)
            .items(items)
            .build();

        SendPushResponseDto responseDto;

        // 실패
        responseDto = pushSender.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage()).isEqualTo(sendPushResponseDto.getMessage());
    }

    @Test
    @DisplayName("Push Type이 T인 경우")
        //else if (pushTypeList[i].equalsIgnoreCase("L")) 까지만 있어 else에 해당하는 조건 추가해야한다.
    void whenPushTypeL() {
        //given
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
            .type(sFlag)
            .message(sMessage)
            .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
            .message(sMessage)
            .service(pushServiceResultDtoArrayList)
            .build();

        List<String> items = new ArrayList<>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("T")
            .sendCode("P001")
            .regType("1")
            .serviceType("C")
            .reserve(reserveMap)
            .items(items)
            .build();

        SendPushResponseDto responseDto = pushSender.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage()).isEqualTo(sendPushResponseDto.getMessage());

    }

    @Test
    @DisplayName("TV이며 ExtraSend N인 경우 테스트")
        //if (serviceTarget.equals("TV") && extraSendYn.equals("Y")) 에 대한 4가지 케이스를 다 만들기 위해
    void whenTVAndExtraSendIsN() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(serviceTargetMap.get("TV"))).when(sendPushCodeProps).findMapByServiceType("TV");
        doReturn(Optional.of(sendCodeMap.get("P002"))).when(sendPushCodeProps).findMapBySendCode("P002");

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
            .type(sFlag)
            .message(sMessage)
            .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
            .message(sMessage)
            .service(pushServiceResultDtoArrayList)
            .build();

        List<String> items = new ArrayList<>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("A|G")
            .sendCode("P002")
            .regType("1")
            .serviceType("C|TV")
            .reserve(reserveMap)
            .items(items)
            .build();

        SendPushResponseDto responseDto = pushSender.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage()).isEqualTo(sendPushResponseDto.getMessage());

    }

    @Test
    @DisplayName("HTTP PUSH에서 오류코드를 제대로 출력되는지 체크")
    void whenResponseFail_ThenReturnException() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(serviceTargetMap.get("TV"))).when(sendPushCodeProps).findMapByServiceType("TV");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException(null, "1113");
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willThrow(httpPushCustomException);

        List<String> items = new ArrayList<String>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G|A")
            .sendCode("P001")
            .regType("1")
            .serviceType("C|TV")
            .reserve(reserveMap)
            .items(items)
            .build();

        assertDoesNotThrow(() -> pushSender.sendPushCode(sendPushCodeRequestDto));
    }

    @Test
    @DisplayName("HTTP PUSH에서 오류코드1118를 제대로 출력되는지 체크")
    void whenResponseFail1108_ThenReturnException() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(serviceTargetMap.get("TV"))).when(sendPushCodeProps).findMapByServiceType("TV");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException(null, "1108");
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willThrow(httpPushCustomException);

        List<String> items = new ArrayList<String>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G|A")
            .sendCode("P001")
            .regType("1")
            .serviceType("C|TV")
            .reserve(reserveMap)
            .items(items)
            .build();

        assertDoesNotThrow(() -> pushSender.sendPushCode(sendPushCodeRequestDto));
    }


    @Test
    @DisplayName("정상적으로 regId를 가져오는 지 확인")
    void getRegistrationID() {
        //given
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .saId("500058151453")
            .stbMac("001c.627e.039c")
            .build();

        //when
        String regId = pushSender.getRegistrationID(sendPushCodeRequestDto);

        //then
        assertThat(regId).isEqualTo(REG_ID);
    }

    @Test
    @DisplayName("ctn을 입력으로 받아 정상적으로 regId 조회하는 지 확인")
    void getRegistrationIDbyCtn() {

        //given
        SaIdDto saIdDto = SaIdDto.builder()
            .saId(SA_ID)
            .build();

        List<SaIdDto> saIdDtos = List.of(saIdDto);

        given(subscriberDomainClient.getRegistrationIDbyCtn(any())).willReturn(saIdDtos);
        String ctn = REG_ID;

        //when
        String regId = pushSender.getRegistrationIDbyCtn(ctn);

        //then
        assertThat(regId).isEqualTo(SA_ID);
    }

    @Test
    @DisplayName("ctn을 입력으로 받아 regId 조회하지 못한 경우 확인")
    void getRegistrationIDbyCtn_Fail() {

        //given
        given(subscriberDomainClient.getRegistrationIDbyCtn(any())).willReturn(List.of());
        String ctn = REG_ID;

        //when
        String regId = pushSender.getRegistrationIDbyCtn(ctn);

        //then
        assertThat(regId).isEmpty();
    }

    @Test
    void getGcmOrTVRequestDto() {

        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        SaIdDto saIdDto = SaIdDto.builder()
            .saId(SA_ID)
            .build();
        List<SaIdDto> saIdDtos = List.of(saIdDto);
        given(subscriberDomainClient.getRegistrationIDbyCtn(any())).willReturn(saIdDtos);

        //serviceType : C
        HttpPushSingleRequestDto result;
        result = pushSender.getGcmOrTVRequestDto(sendPushCodeRequestDto, "C", "G");
        assertThat(result.getApplicationId()).isEqualTo("musicshow_gcm");
        assertThat(result.getServiceId()).isEqualTo("30104");

        //serviceType : H
        result = pushSender.getGcmOrTVRequestDto(sendPushCodeRequestDto, "H", "G");
        assertThat(result.getApplicationId()).isEqualTo("hdtv_GCM01");
        assertThat(result.getServiceId()).isEqualTo("20014");

        //serviceType : ""
        result = pushSender.getGcmOrTVRequestDto(sendPushCodeRequestDto, "", "G");
        assertThat(result.getApplicationId()).isEqualTo("hdtv_GCM01");
        assertThat(result.getServiceId()).isEqualTo("20014");

        result = pushSender.getGcmOrTVRequestDto(sendPushCodeRequestDto, "Invalid", "Invalid");
        assertThat(result.getApplicationId()).isEqualTo("");
        assertThat(result.getServiceId()).isEqualTo("");

        //regType : 2
        sendPushCodeRequestDto.setRegType("2");
        result = pushSender.getGcmOrTVRequestDto(sendPushCodeRequestDto, "", "G");
        assertThat(result.getApplicationId()).isEqualTo("hdtv_GCM01");
        assertThat(result.getServiceId()).isEqualTo("20014");

        Exception exception = assertThrows(InvalidSendPushCodeException.class, () -> {
            pushSender.getGcmOrTVRequestDto(sendPushCodeRequestDto, "", "L");
        });

        assertThat(exception.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException");

        Exception exception2 = assertThrows(InvalidSendPushCodeException.class, () -> {
            pushSender.getApnsRequestDto(sendPushCodeRequestDto, "", "L");
        });

        assertThat(exception2.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException");

    }

    @Test
    @DisplayName("InvalidSendPushCodeException 처리가 잘되는 지 테스트")
    void checkInvalidSendPushCode() {

        //given
        Map<String, String> pushInfoMap = new HashMap<>();
        pushInfoMap.put("gcm.payload.body", "tesst");

        pushSender.checkGCMBody(pushInfoMap);

        Map<String, String> pushInfoMapE = new HashMap<>();
        pushInfoMapE.put("gcm.payload.body", "");
        //sendcode 미지원 exceptioin
        Exception exception = assertThrows(InvalidSendPushCodeException.class, () -> {
            pushSender.checkGCMBody(pushInfoMapE);
        });
        assertThat(exception.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException");

    }

    @Test
    @DisplayName("pushType이 APNS일 경우 requestDto조립 테스트")
    void getApnsRequestDto() {

        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        SaIdDto saIdDto = SaIdDto.builder()
            .saId(SA_ID)
            .build();
        List<SaIdDto> saIdDtos = List.of(saIdDto);
        given(subscriberDomainClient.getRegistrationIDbyCtn(any())).willReturn(saIdDtos);

        HttpPushSingleRequestDto result;
        //pushType : "A"
        result = pushSender.getApnsRequestDto(sendPushCodeRequestDto, "C", "A");
        assertThat(result.getApplicationId()).isEqualTo("musicshow_apns");
        assertThat(result.getServiceId()).isEqualTo("30107");

        //regType : 2
        sendPushCodeRequestDto.setRegType("2");
        result = pushSender.getApnsRequestDto(sendPushCodeRequestDto, "C", "A");
        assertThat(result.getApplicationId()).isEqualTo("musicshow_apns");
        assertThat(result.getServiceId()).isEqualTo("30107");

        //serviceType : H
        result = pushSender.getApnsRequestDto(sendPushCodeRequestDto, "H", "A");
        assertThat(result.getApplicationId()).isEqualTo("hdtv_APNS1");
        assertThat(result.getServiceId()).isEqualTo("30005");

        //serviceType : ""
        result = pushSender.getApnsRequestDto(sendPushCodeRequestDto, "", "A");
        assertThat(result.getApplicationId()).isEqualTo("hdtv_APNS1");
        assertThat(result.getServiceId()).isEqualTo("30005");


    }

    @Test
    @DisplayName("소켓푸시일 경우 requestDto조립 테스트")
    void getExtraPushRequestDto() {

        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        List<PushRequestItemDto> addItems = new ArrayList<>();
        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        //given
        PushRequestSingleDto pushRequestSingleDto = PushRequestSingleDto.builder()
            .applicationId("30015")
            .serviceId("smartuxapp")
            .pushType("L")
            .message("\"result\":{\"noti_type\":\"PA_TM\", \"address\":\"111111\", \"unumber\":\"948-0719\",\"req_date\":\"202002141124\",\"ctn\":\"\",\"trans_id\":\"\"}")
            .regId("M00020200205")
            .items(addItems)
            .build();

        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        //serviceType : C
        PushRequestSingleDto result;
        result = pushSender.getExtraPushRequestDto(sendPushCodeRequestDto, "C", "G");
        assertThat(result.getApplicationId()).isEqualTo("smartuxapp");
        assertThat(result.getServiceId()).isEqualTo("30015");

        //serviceType : H
        result = pushSender.getExtraPushRequestDto(sendPushCodeRequestDto, "H", "G");
        assertThat(result.getApplicationId()).isEqualTo("smartuxapp");
        assertThat(result.getServiceId()).isEqualTo("30015");

        //serviceType : ""
        result = pushSender.getExtraPushRequestDto(sendPushCodeRequestDto, "", "G");
        assertThat(result.getApplicationId()).isEqualTo("smartuxapp");
        assertThat(result.getServiceId()).isEqualTo("30015");

        //pushType : "A"
        result = pushSender.getExtraPushRequestDto(sendPushCodeRequestDto, "", "A");
        assertThat(result.getApplicationId()).isEqualTo("smartuxapp");
        assertThat(result.getServiceId()).isEqualTo("30015");

        //regType : 2
        sendPushCodeRequestDto.setRegType("2");
        result = pushSender.getExtraPushRequestDto(sendPushCodeRequestDto, "", "G");
        assertThat(result.getApplicationId()).isEqualTo("smartuxapp");
        assertThat(result.getServiceId()).isEqualTo("30015");


    }


    @Test
    @DisplayName("HTTP PUSH에서 오류코드가 1113 혹은 1108이 아닌 경우 테스트")
        //if (failCode.equals("1113") || failCode.equals("1108")) else 체크
    void whenResponseFailHttp_ThenReturnAnotherExceptionCode() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException(null, "1004");
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willThrow(httpPushCustomException);

        ArrayList<String> items = new ArrayList<>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G|A")
            .sendCode("P001")
            .regType("1")
            .serviceType("C")
            .reserve(reserveMap)
            .items(items)
            .build();

        SendPushResponseDto result = pushSender.sendPushCode(sendPushCodeRequestDto);
        assertThat(result.getFlag()).isEqualTo(httpPushCustomException.getCode());
    }

    @Test
    @DisplayName("서비스별 성공실패 기록 로직에서 분기 체크")
        //else if (chk1001 > 0) 체크
    void whenResponseFailHttp_ThenReturnChk1001() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("C"))).when(sendPushCodeProps).findMapByServiceType("C");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException(null, "1113");
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willThrow(httpPushCustomException);

        List<String> items = new ArrayList<>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G")
            .sendCode("P001")
            .regType("1")
            .serviceType("C")
            .reserve(reserveMap)
            .items(items)
            .build();

        assertDoesNotThrow(() -> pushSender.sendPushCode(sendPushCodeRequestDto));
    }

    @Test
    @DisplayName("소켓 PUSH에서 오류코드를 제대로 출력되는지 체크")
    void whenResponseFailSocket_ThenReturnException() {
        //given
        doReturn(Optional.of(serviceTargetMap.get("default"))).when(sendPushCodeProps).findMapByServiceType("default");
        doReturn(Optional.of(serviceTargetMap.get("TV"))).when(sendPushCodeProps).findMapByServiceType("TV");
        doReturn(Optional.of(sendCodeMap.get("P001"))).when(sendPushCodeProps).findMapBySendCode("P001");

        given(pushSingleDomainService.requestPushSingle(any())).willThrow(new PushEtcException());
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        List<String> items = new ArrayList<>();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address", "111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
            .registrationId("M00020200205")
            .pushType("G")
            .sendCode("P001")
            .regType("1")
            .serviceType("TV")
            .reserve(reserveMap)
            .items(items)
            .build();

        assertDoesNotThrow(() -> pushSender.sendPushCode(sendPushCodeRequestDto));
    }
}
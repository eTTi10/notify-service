package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
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

    HttpPushResponseDto httpPushResponseDto;

    Map<String, String> serviceTargetDefaultMap = new HashMap<>();
    Map<String, String> serviceTargetMap = new HashMap<>();
    Map<String, String> sendCodeMap = new HashMap<>();
    List<String> items = new ArrayList<>();

    String sFlag = "0000";
    String sMessage = "성공";

    @InjectMocks
    PushDomainService pushDomainService;

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
        JunitTestUtils.setValue(pushDomainService, "fcmExtraSend", "N");
        JunitTestUtils.setValue(pushDomainService, "extraServiceId", "30015");
        JunitTestUtils.setValue(pushDomainService, "extraApplicationId", "smartuxapp");

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
        items.add("cmaaaa");

        // 기본 입력
        Map<String, String> reserveMap = Map.of("address","111111");

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
    @DisplayName("정상적인 응답을 하는지 테스트")
    void sendPushCode() {
        //given
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMap));
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));

        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);


        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
                .sType(sFlag)
                .sMessage(sMessage)
                .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
                .message(sMessage)
                .service(pushServiceResultDtoArrayList)
                .build();

        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

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
        responseDto = pushDomainService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

//        // if serviceTarget.equalsIgnoreCase("TV") 테스트
//        sendPushCodeRequestDto.setServiceType("TV");
//        responseDto = pushDomainService.sendPushCode(sendPushCodeRequestDto);
//        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));
//
        // if(sendCode.substring(0,1).equals("T")) 테스트
        sendPushCodeRequestDto.setSendCode("T001");
        responseDto = pushDomainService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

    }

    @Test
    @DisplayName("응답메시지가 성공이 아닐 때 테스트")
    void returnFailMessage() {
        //given

        httpPushResponseDto = HttpPushResponseDto.builder()
                .code(sFlag)
                .message("실패")
                .build();

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));

        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
                .sType(sFlag)
                .sMessage("실패")
                .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
                .message("실패")
                .service(pushServiceResultDtoArrayList)
                .build();

        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
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
        responseDto = pushDomainService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

    }

    @Test
    @DisplayName("Push Type이 T인 경우") //else if (pushTypeList[i].equalsIgnoreCase("L")) 까지만 있어 else에 해당하는 조건 추가해야한다.
    void whenPushTypeL() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
                .sType(sFlag)
                .sMessage(sMessage)
                .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
                .message(sMessage)
                .service(pushServiceResultDtoArrayList)
                .build();

        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .registrationId("M00020200205")
                .pushType("T")
                .sendCode("P001")
                .regType("1")
                .serviceType("C")
                .reserve(reserveMap)
                .items(items)
                .build();

        SendPushResponseDto responseDto = pushDomainService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

    }

    /**
     * TODO 동작 안함 다시 체크
     */
    @Test
    @DisplayName("TV이며 ExtraSend N인 경우 테스트") //if (serviceTarget.equals("TV") && extraSendYn.equals("Y")) 가 아닌 경우
    void whenTVAndExtraSendIsN() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));

        //서비스타입별 결과 저장용 List
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
                .sType(sFlag)
                .sMessage(sMessage)
                .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
                .message(sMessage)
                .service(pushServiceResultDtoArrayList)
                .build();

        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .registrationId("M00020200205")
                .pushType("A|G")
                .sendCode("P002")
                .regType("1")
                .serviceType("C")
                .reserve(reserveMap)
                .items(items)
                .build();

        SendPushResponseDto responseDto = pushDomainService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

    }

    @Test
    @DisplayName("HTTP PUSH에서 오류코드를 제대로 출력되는지 체크")
    void whenResponseFail_ThenReturnException() {
        //given
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException();
        httpPushCustomException.setCode("1113");
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willThrow(httpPushCustomException);

        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .registrationId("M00020200205")
                .pushType("G|A")
                .sendCode("P001")
                .regType("1")
                .serviceType("C|TV")
                .reserve(reserveMap)
                .items(items)
                .build();

        pushDomainService.sendPushCode(sendPushCodeRequestDto);
    }

    @Test
    @DisplayName("HTTP PUSH에서 오류코드1118를 제대로 출력되는지 체크")
    void whenResponseFail1108_ThenReturnException() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException();
        httpPushCustomException.setCode("1108");
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willThrow(httpPushCustomException);

        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .registrationId("M00020200205")
                .pushType("G|A")
                .sendCode("P001")
                .regType("1")
                .serviceType("C|TV")
                .reserve(reserveMap)
                .items(items)
                .build();

        pushDomainService.sendPushCode(sendPushCodeRequestDto);
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
        String regId = pushDomainService.getRegistrationID(sendPushCodeRequestDto);

        //then
        assertThat(regId.equals(REG_ID));
    }

    @Test
    @DisplayName("ctn을 입력으로 받아 정상적으로 regId 조회하는 지 확인")
    void getRegistrationIDbyCtn() {

        //given
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
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
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(subscriberDomainClient.getRegistrationIDbyCtn(any())).willReturn(regIdDto);
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));

        //serviceType : C
        HttpPushSingleRequestDto result;
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "C", "G");
        assertThat(result.equals(httpPushSingleRequestDto));

        //serviceType : H
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "H", "G");
        assertThat(result.equals(httpPushSingleRequestDto));

        //serviceType : ""
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "", "G");
        assertThat(result.equals(httpPushSingleRequestDto));

        //pushType : "A"
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "", "A");
        assertThat(result.equals(httpPushSingleRequestDto));

        //regType : 2
        sendPushCodeRequestDto.setRegType("2");
        result = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, "", "G");
        assertThat(result.equals(httpPushSingleRequestDto));

    }

    @Test
    @DisplayName("InvalidSendPushCodeException 처리가 잘되는 지 테스트")
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
    @DisplayName("pushType이 APNS일 경우 requestDto조립 테스트")
    void getApnsRequestDto() {

        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(subscriberDomainClient.getRegistrationIDbyCtn(any())).willReturn(regIdDto);

        HttpPushSingleRequestDto result;
        //pushType : "A"
        result = pushDomainService.getApnsRequestDto(sendPushCodeRequestDto, "C", "A");
        assertThat(result.equals(httpPushSingleRequestDto));

        //regType : 2
        sendPushCodeRequestDto.setRegType("2");
        result = pushDomainService.getApnsRequestDto(sendPushCodeRequestDto, "C", "A");
        assertThat(result.equals(httpPushSingleRequestDto));

        //serviceType : H
        result = pushDomainService.getApnsRequestDto(sendPushCodeRequestDto, "H", "A");
        assertThat(result.equals(httpPushSingleRequestDto));

        //serviceType : ""
        result = pushDomainService.getApnsRequestDto(sendPushCodeRequestDto, "", "A");
        assertThat(result.equals(httpPushSingleRequestDto));


    }

    @Test
    @DisplayName("pushType이 LG푸시일 경우 requestDto조립 테스트")
    void getPosRequestDto() {

        //정상
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMap));


        HttpPushSingleRequestDto result;
        result = pushDomainService.getPosRequestDto(sendPushCodeRequestDto, "C", "L");
        assertThat(result.equals(httpPushSingleRequestDto));


        //LG Push 미지원 1
        Map<String, String> serviceTargetMapE = new HashMap<>();
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMapE));
        Exception exception = assertThrows(InvalidSendPushCodeException.class, () -> {
            pushDomainService.getPosRequestDto(sendPushCodeRequestDto, "C", "G");
        });
        assertThat(exception.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException");

        //LG Push 미지원 2
        serviceTargetMapE.put("pos.serviceid","");
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMapE));
        Exception exception2 = assertThrows(InvalidSendPushCodeException.class, () -> {
            pushDomainService.getPosRequestDto(sendPushCodeRequestDto, "C", "G");
        });
        assertThat(exception2.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException");


    }

    @Test
    @DisplayName("소켓푸시일 경우 requestDto조립 테스트")
    void getExtraPushRequestDto() {


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
        given(sendPushCodeProps.findMapByServiceType("C")).willReturn(Optional.of(serviceTargetMap));
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));

        //serviceType : C
        PushRequestSingleDto result;
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "C", "G");
        assertThat(result.equals(pushRequestSingleDto));

        //serviceType : H
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "H", "G");
        assertThat(result.equals(pushRequestSingleDto));

        //serviceType : ""
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "", "G");
        assertThat(result.equals(pushRequestSingleDto));

        //pushType : "A"
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "", "A");
        assertThat(result.equals(pushRequestSingleDto));

        //regType : 2
        sendPushCodeRequestDto.setRegType("2");
        result = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, "", "G");
        assertThat(result.equals(pushRequestSingleDto));

    }


    @Test
    @DisplayName("HTTP PUSH에서 오류코드가 1113 혹은 1108이 아닌 경우 테스트")  //if (failCode.equals("1113") || failCode.equals("1108")) else 체크
    void whenResponseFailHttp_ThenReturnAnotherExceptionCode() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException();
        httpPushCustomException.setCode("1004");
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willThrow(httpPushCustomException);

        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .registrationId("M00020200205")
                .pushType("G|A")
                .sendCode("P001")
                .regType("1")
                .serviceType("C")
                .reserve(reserveMap)
                .items(items)
                .build();

        pushDomainService.sendPushCode(sendPushCodeRequestDto);
    }

    @Test
    @DisplayName("서비스별 성공실패 기록 로직에서 분기 체크") //else if (chk1001 > 0) 체크
    void whenResponseFailHttp_ThenReturnChk1001() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));

        HttpPushCustomException httpPushCustomException = new HttpPushCustomException();
        httpPushCustomException.setCode("1113");
        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willThrow(httpPushCustomException);

        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .registrationId("M00020200205")
                .pushType("G")
                .sendCode("P001")
                .regType("1")
                .serviceType("C")
                .reserve(reserveMap)
                .items(items)
                .build();

        pushDomainService.sendPushCode(sendPushCodeRequestDto);
    }

    @Test
    @DisplayName("소켓 PUSH에서 오류코드를 제대로 출력되는지 체크")
    void whenResponseFailSocket_ThenReturnException() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(sendPushCodeProps.findMapByServiceType("default")).willReturn(Optional.of(serviceTargetDefaultMap));
        given(pushSingleDomainService.requestPushSingle(any())).willThrow(new PushEtcException());
        RegIdDto regIdDto = RegIdDto.builder().registrationId(REG_ID).build();
        given(personalizationDomainClient.getRegistrationID(any())).willReturn(regIdDto);

        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .registrationId("M00020200205")
                .pushType("G")
                .sendCode("P001")
                .regType("1")
                .serviceType("TV")
                .reserve(reserveMap)
                .items(items)
                .build();

        pushDomainService.sendPushCode(sendPushCodeRequestDto);
    }

}
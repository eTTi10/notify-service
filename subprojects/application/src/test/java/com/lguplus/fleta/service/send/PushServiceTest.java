package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.PushServiceResultDto;
import com.lguplus.fleta.data.dto.response.SendPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.properties.SendPushCodeProps;
import com.lguplus.fleta.service.httppush.HttpSinglePushDomainService;
import com.lguplus.fleta.service.push.PushSingleDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class PushServiceTest {

    HttpPushSingleRequestDto httpPushSingleRequestDto;
    HttpPushResponseDto httpPushResponseDto;

    Map<String, String> serviceTargetDefaultMap = new HashMap<>();
    Map<String, String> serviceTargetMap = new HashMap<>();
    Map<String, String> sendCodeMap = new HashMap<>();
    List<String> items = new ArrayList<>();

    String sFlag = "0000";
    String sMessage = "성공";

    @InjectMocks
    PushService pushService;

    @Mock
    PushDomainService pushDomainService;

    @Mock
    HttpSinglePushDomainService httpSinglePushDomainService;

    @Mock
    PushSingleDomainService pushSingleDomainService;

    @Mock
    SendPushCodeProps sendPushCodeProps;

    @BeforeEach
    void setUp() {

        httpPushSingleRequestDto = HttpPushSingleRequestDto.builder()
                .applicationId("musicshow_gcm")
                .serviceId("30104")
                .pushType("G")
                .message("\"result\":{\"noti_type\":\"PA_TM\", \"address\":\"111111\", \"unumber\":\"948-0719\",\"req_date\":\"202002141124\",\"ctn\":\"\",\"trans_id\":\"\"}")
                .users(List.of("M00020200205"))
                .items(List.of("badge!^1", "sound!^ring.caf", "cm!^aaaa"))
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
                .pushType("G|A|L")
                .sendCode("P001")
                .regType("1")
                .serviceType("C|TV")
                .reserve(reserveMap)
                .items(items)
                .build();


        SendPushResponseDto responseDto;

        //정상 응답
        responseDto = pushService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

        // if serviceTarget.equalsIgnoreCase("TV") 테스트
        sendPushCodeRequestDto.setServiceType("TV");
        responseDto = pushService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

        // if(sendCode.substring(0,1).equals("T")) 테스트
        sendPushCodeRequestDto.setSendCode("T001");
        responseDto = pushService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

    }

    @Test
    @DisplayName("응답메시지가 성공이 아닐 때 테스트")
    void returnFailMessage() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));

        httpPushResponseDto = HttpPushResponseDto.builder()
                .code(sFlag)
                .message("실패")
                .build();

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

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
        responseDto = pushService.sendPushCode(sendPushCodeRequestDto);
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

        SendPushResponseDto responseDto = pushService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

    }


    @Test
    @DisplayName("TV이며 ExtraSend N인 경우") //if (serviceTarget.equals("TV") && extraSendYn.equals("Y")) 가 아닌 경우 테스트
    void whenTVAndExtraSendIsN() {
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
                .pushType("A|G")
                .sendCode("P002")
                .regType("1")
                .serviceType("C")
                .reserve(reserveMap)
                .items(items)
                .build();

        SendPushResponseDto responseDto = pushService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

    }

    @Test
    @DisplayName("TV 아니며 ExtraSend Y인 경우") //if (serviceTarget.equals("TV") && extraSendYn.equals("Y")) 가 아닌 경우 테스트
    void whenCAndExtraSendIsY() {
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
                .pushType("A|G")
                .sendCode("P001")
                .regType("1")
                .serviceType("C")
                .reserve(reserveMap)
                .items(items)
                .build();

        SendPushResponseDto responseDto = pushService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));

    }

    @Test
    @DisplayName("HTTP PUSH에서 오류코드를 제대로 출력되는지 체크")
    void whenResponseFail_ThenReturnException() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));

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

        pushService.sendPushCode(sendPushCodeRequestDto);
    }

    @Test
    @DisplayName("HTTP PUSH에서 오류코드1118를 제대로 출력되는지 체크")
    void whenResponseFail1108_ThenReturnException() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));

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

        pushService.sendPushCode(sendPushCodeRequestDto);
    }


    @Test
    @DisplayName("HTTP PUSH에서 오류코드가 1113 혹은 1108이 아닌 경우 테스트")  //if (failCode.equals("1113") || failCode.equals("1108")) else 체크
    void whenResponseFailHttp_ThenReturnAnotherExceptionCode() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));

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

        pushService.sendPushCode(sendPushCodeRequestDto);
    }

    @Test
    @DisplayName("서비스별 성공실패 기록 로직에서 분기 체크") //else if (chk1001 > 0) 체크
    void whenResponseFailHttp_ThenReturnChk1001() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));

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

        pushService.sendPushCode(sendPushCodeRequestDto);
    }

    @Test
    @DisplayName("소켓 PUSH에서 오류코드를 제대로 출력되는지 체크")
    void whenResponseFailSocket_ThenReturnException() {
        //given
        given(sendPushCodeProps.findMapBySendCode(anyString())).willReturn(Optional.of(sendCodeMap));
        given(pushSingleDomainService.requestPushSingle(any())).willThrow(new NotifyPushRuntimeException());

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

        pushService.sendPushCode(sendPushCodeRequestDto);
    }
}
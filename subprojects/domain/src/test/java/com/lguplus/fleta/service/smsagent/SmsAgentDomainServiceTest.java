package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.SmsAgentClient;
import com.lguplus.fleta.client.SettingDomainClient;
import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class SmsAgentDomainServiceTest {

    String sFlag = "0000";
    String sMessage = "성공";

    @InjectMocks
    SmsAgentDomainService smsAgentDomainService;

    @Mock
    SmsAgentClient smsAgentClient;

    @Mock
    SettingDomainClient apiClient;

    SmsGatewayResponseDto smsGatewayResponseDto;

    @BeforeEach
    void setUp() {

        // mock object
        smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                .flag(sFlag)
                .message(sMessage)
                .build();


        ReflectionTestUtils.setField(smsAgentDomainService, "smsSenderNo", "01011112222");

        ReflectionTestUtils.setField(smsAgentDomainService, "codePhoneNumberErrorException", "1500");
        ReflectionTestUtils.setField(smsAgentDomainService, "codeMsgTypeErrorException", "1500");
        ReflectionTestUtils.setField(smsAgentDomainService, "codeSystemBusyException", "1503");
        ReflectionTestUtils.setField(smsAgentDomainService, "codeSystemErrorException", "1500");

        ReflectionTestUtils.setField(smsAgentDomainService, "codeEtcException", "9999");
        ReflectionTestUtils.setField(smsAgentDomainService, "messageEtcException", "기타 오류");
    }

    @Test
    @DisplayName("메시지 입력받는 SMS발송 테스트")
    void sendSms() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        //requestDto
        SendSmsRequestDto request = SendSmsRequestDto.builder()
                .rCtn("01051603997")
                .sCtn("01051603997")
                .msg("문자내용")
                .build();

        /* 1 SmsAgentEtcException */
        SmsAgentCustomException exception;
        exception = assertThrows(SmsAgentCustomException.class, () -> {
            smsAgentDomainService.sendSms(request);
        });
        assertThat(exception.getClass()).isEqualTo(SmsAgentCustomException.class);

        /* 정상리턴 */
        ReflectionTestUtils.setField(smsAgentDomainService, "agentNoSendUse", "0");
        given(smsAgentClient.send(anyString(), anyString(), anyString())).willReturn(smsGatewayResponseDto);
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSms(request);
        assertThat(responseDto.getFlag()).isEqualTo(smsGatewayResponseDto.getFlag());


        /* agentNoSendTime 빈값일때 ServerSettingInfoException */
        ReflectionTestUtils.setField(smsAgentDomainService, "agentNoSendUse", "1");
        exception = assertThrows(SmsAgentCustomException.class, () -> {
            smsAgentDomainService.sendSms(request);
        });
        assertThat(exception.getCode()).isEqualTo("5200");

        /* startTime이 endTime 보다 크거나 같을 때 */
        ReflectionTestUtils.setField(smsAgentDomainService, "agentNoSendTime", "23|06");
        assertDoesNotThrow(() -> smsAgentDomainService.sendSms(request));

        /* 전송할 수 있는 시간이 아닐 때 NotSendTimeException */
        ReflectionTestUtils.setField(smsAgentDomainService, "agentNoSendTime", "03|23");
        exception = assertThrows(SmsAgentCustomException.class, () -> {
            smsAgentDomainService.sendSms(request);
        });
        assertThat(exception.getCode()).isEqualTo("1504");

        /* 전송할 수 있는 시간이 아닐 때 */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
        LocalTime now = LocalTime.now().minusHours(2);
        String startHour = now.format(formatter);  //현재 시간 -2
        String endHour = now.plusHours(1).format(formatter); //현재 시간 -1

        ReflectionTestUtils.setField(smsAgentDomainService, "agentNoSendTime", startHour+"|"+endHour);
        assertDoesNotThrow(() -> smsAgentDomainService.sendSms(request));
    }

    @Test
    @DisplayName("코드를 이용한 SMS발송 테스트")
    void sendSmsCode() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);

        given(smsAgentClient.send(anyString(), anyString(), anyString())).willReturn(smsGatewayResponseDto);

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        assertThat(responseDto.getFlag()).isEqualTo(smsGatewayResponseDto.getFlag());


        // convertMsg 함수 용 replacement 공백 테스트
        SendSmsCodeRequestDto noReplacementRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("")
                .build();
        responseDto = smsAgentDomainService.sendSmsCode(noReplacementRequestDto);
        assertThat(responseDto.getFlag()).isEqualTo(smsGatewayResponseDto.getFlag());

    }

    @Test
    @DisplayName("Interrupted Exception 코드를 이용한 SMS발송 테스트")
    void sendSmsCode_InterruptedException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentClient.send(anyString(), anyString(), anyString())).willThrow(new InterruptedException());
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        assertThat(responseDto.getFlag()).isEqualTo("9999");
    }

    @Test
    @DisplayName("Exception 코드를 이용한 SMS발송 테스트")
    void sendSmsCode_Exception() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentClient.send(anyString(), anyString(), anyString())).willThrow(new NullPointerException());
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        assertThat(responseDto.getFlag()).isEqualTo("9999");

    }

    @Test
    @DisplayName("PhoneNumberErrorException 테스트")
    void sendSmsCode_PhoneNumberErrorException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);

        SmsAgentCustomException smsAgentCustomException = new SmsAgentCustomException("1502", "전화번호 형식 오류");
        given(smsAgentClient.send(anyString(), anyString(), anyString())).willThrow(smsAgentCustomException);

        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        assertThat(responseDto.getFlag()).isEqualTo("1502");

    }

    @Test
    @DisplayName("MsgTypeErrorException 테스트")
    void sendSmsCode_MsgTypeErrorException () throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);
        SmsAgentCustomException smsAgentCustomException = new SmsAgentCustomException("1501", "전화번호 형식 오류");
        given(smsAgentClient.send(anyString(), anyString(), anyString())).willThrow(smsAgentCustomException);
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        assertThat(responseDto.getFlag()).isEqualTo("1501");
    }

    @Test
    @DisplayName("SystemBusyException  테스트")
    void sendSmsCode_SystemBusyException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);
        SmsAgentCustomException smsAgentCustomException = new SmsAgentCustomException("1503", "메시지 처리 수용 한계 초과");
        given(smsAgentClient.send(anyString(), anyString(), anyString())).willThrow(smsAgentCustomException);
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        assertThat(responseDto.getFlag()).isEqualTo("1503");
    }

    @Test
    @DisplayName("SystemErrorException   테스트")
    void sendSmsCode_SystemErrorException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);
        SmsAgentCustomException smsAgentCustomException = new SmsAgentCustomException("1500", "시스템 장애");

        given(smsAgentClient.send(anyString(), anyString(), anyString())).willThrow(smsAgentCustomException);
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        assertThat(responseDto.getFlag()).isEqualTo("1500");
    }

    @Test
    @DisplayName("SmsAgentEtcException 테스트")
    void sendSmsCode_SmsAgentEtcException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);
        SmsAgentCustomException smsAgentCustomException = new SmsAgentCustomException("9999", "기타 오류");
        given(smsAgentClient.send(anyString(), anyString(), anyString())).willThrow(smsAgentCustomException);
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        assertThat(responseDto.getFlag()).isEqualTo("9999");
    }

    @Test
    @DisplayName("해당 코드에 존재하는 메시지가 없음 예외처리 테스트")
    void callSettingApi_returnTotalCountZero() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(0)
                        .data(dto)
                        .build())
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        assertThrows(NotFoundMsgException.class, () -> {
            smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        });
    }


    @Test
    @DisplayName("callSettingApi함수 Exception 테스트")
    void sendSmsCode_Exception2() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        given(apiClient.callSettingApi(any())).willThrow(new SmsAgentEtcException());
        assertThrows(SmsAgentEtcException.class, () -> {
            smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        });
    }



    @Test
    @DisplayName("retrySmsSend함수의 InterruptedException 테스트")
    void retrySmsSend_InterruptedException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        //Define a thread for interrupts
        final class InterruptThread extends Thread {
            Thread targetThread = null;

            public InterruptThread(Thread thread) {
                targetThread = thread;
            }

            @Override
            public void run() {
                targetThread.interrupt();
            }
        }

        //Start an interrupt thread
        InterruptThread th = new InterruptThread(Thread.currentThread());
        th.start();

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .code("0000")
                .message("성공")
                .result(CallSettingResultDto.builder()
                        .dataCount(1)
                        .data(dto)
                        .build())
                .build();

        given(apiClient.callSettingApi(any())).willReturn(resultMapDto);
        SmsAgentCustomException smsAgentCustomException = new SmsAgentCustomException("1503", "메시지 처리 수용 한계 초과");
        given(smsAgentClient.send(anyString(), anyString(), anyString())).willThrow(smsAgentCustomException);

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        // when
        SmsAgentEtcException exception = assertThrows(SmsAgentEtcException.class, () -> {
            smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(SmsAgentEtcException.class);

    }



}
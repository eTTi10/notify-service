package com.lguplus.fleta.service.smsagent;

import com.lguplus.fleta.client.SmsAgentDomainClient;
import com.lguplus.fleta.client.SmsCallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.exception.smsagent.*;
import com.lguplus.fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    SmsAgentDomainClient smsAgentDomainClient;

    @Mock
    SmsCallSettingDomainClient apiClient;

    SmsGatewayResponseDto smsGatewayResponseDto;

    @BeforeEach
    void setUp() {

        // mock object
        smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                .flag(sFlag)
                .message(sMessage)
                .build();


        JunitTestUtils.setValue(smsAgentDomainService, "smsSenderNo", "01011112222");

        JunitTestUtils.setValue(smsAgentDomainService, "codePhoneNumberErrorException", "1500");
        JunitTestUtils.setValue(smsAgentDomainService, "codeMsgTypeErrorException", "1500");
        JunitTestUtils.setValue(smsAgentDomainService, "codeSystemBusyException", "1503");
        JunitTestUtils.setValue(smsAgentDomainService, "codeSystemErrorException", "1500");

        JunitTestUtils.setValue(smsAgentDomainService, "codeEtcException", "9999");
        JunitTestUtils.setValue(smsAgentDomainService, "messageEtcException", "기타 오류");


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

        /* 1 */
        Exception exception;
        exception = assertThrows(SmsAgentEtcException.class, () -> {
            smsAgentDomainService.sendSms(request);
        });
        assertThat(exception.getClass().getName()).isEqualTo("com.lguplus.fleta.exception.smsagent.SmsAgentEtcException");

        /* 정상리턴 */
        JunitTestUtils.setValue(smsAgentDomainService, "agentNoSendUse", "0");
        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willReturn(smsGatewayResponseDto);
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSms(request);
        assertThat(responseDto.getFlag().equals(smsGatewayResponseDto.getFlag()));


        /* agentNoSendTime 빈값일때 */
        JunitTestUtils.setValue(smsAgentDomainService, "agentNoSendUse", "1");
        exception = assertThrows(ServerSettingInfoException.class, () -> {
            smsAgentDomainService.sendSms(request);
        });

        /* startTime이 endTime 보다 크거나 같을 때 */
        JunitTestUtils.setValue(smsAgentDomainService, "agentNoSendTime", "23|06");
        smsAgentDomainService.sendSms(request);

        /* 전송할 수 있는 시간이 아닐 때 */
        JunitTestUtils.setValue(smsAgentDomainService, "agentNoSendTime", "03|23");
        exception = assertThrows(NotSendTimeException.class, () -> {
            smsAgentDomainService.sendSms(request);
        });

        /* 전송할 수 있는 시간이 아닐 때 */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
        LocalTime now = LocalTime.now().minusHours(2);
        String startHour = now.format(formatter);  //현재 시간 -2
        String endHour = now.plusHours(1).format(formatter); //현재 시간 -1

        JunitTestUtils.setValue(smsAgentDomainService, "agentNoSendTime", startHour+"|"+endHour);
        smsAgentDomainService.sendSms(request);

    }
    /*
    @Test
    @DisplayName("코드를 이용한 SMS발송 테스트")
    void sendSmsCode() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .code("S001")
                .name("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(1)
                        .memberGroup("")
                        .recordset(List.of(dto))
                        .build())
                .build();

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);

        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willReturn(smsGatewayResponseDto);

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();
        SmsGatewayResponseDto responseDto = smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
        assertThat(responseDto.getFlag().equals(smsGatewayResponseDto.getFlag()));


        // convertMsg 함수 용 replacement 공백 테스트
        SendSmsCodeRequestDto noReplacementRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("")
                .build();
        smsAgentDomainService.sendSmsCode(noReplacementRequestDto);

    }

    @Test
    @DisplayName("Interrupted Exception 코드를 이용한 SMS발송 테스트")
    void sendSmsCode_InterruptedException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .codeId("S001")
                .codeName("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(1)
                        .memberGroup("")
                        .recordset(List.of(dto))
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

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willThrow(new InterruptedException());
        smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);

    }

    @Test
    @DisplayName("Exception 코드를 이용한 SMS발송 테스트")
    void sendSmsCode_Exception() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .codeId("S001")
                .codeName("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(1)
                        .memberGroup("")
                        .recordset(List.of(dto))
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

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willThrow(new NullPointerException());
        smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);

    }

    @Test
    @DisplayName("PhoneNumberErrorException 테스트")
    void sendSmsCode_PhoneNumberErrorException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .codeId("S001")
                .codeName("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(1)
                        .memberGroup("")
                        .recordset(List.of(dto))
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

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willThrow(new PhoneNumberErrorException());
        smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
    }

    @Test
    @DisplayName("PhoneNumberErrorException 테스트")
    void sendSmsCode_MsgTypeErrorException () throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .codeId("S001")
                .codeName("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(1)
                        .memberGroup("")
                        .recordset(List.of(dto))
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

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willThrow(new MsgTypeErrorException ());
        smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
    }

    @Test
    @DisplayName("SystemBusyException  테스트")
    void sendSmsCode_SystemBusyException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .codeId("S001")
                .codeName("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(1)
                        .memberGroup("")
                        .recordset(List.of(dto))
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

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willThrow(new SystemBusyException());
        smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
    }

    @Test
    @DisplayName("SystemErrorException   테스트")
    void sendSmsCode_SystemErrorException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .codeId("S001")
                .codeName("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(1)
                        .memberGroup("")
                        .recordset(List.of(dto))
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

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willThrow(new SystemErrorException());
        smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
    }

    @Test
    @DisplayName("SmsAgentEtcException 테스트")
    void sendSmsCode_SmsAgentEtcException() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingDto dto = CallSettingDto.builder()
                .codeId("S001")
                .codeName("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(1)
                        .memberGroup("")
                        .recordset(List.of(dto))
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

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willThrow(new SmsAgentEtcException());
        smsAgentDomainService.sendSmsCode(sendSmsCodeRequestDto);
    }

    @Test
    @DisplayName("해당 코드에 존재하는 메시지가 없음 예외처리 테스트")
    void callSettingApi_returnTotalCountZero() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(0)
                        .memberGroup("")
                        .recordset(List.of())
                        .build())
                .build();

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);

        // mock object
        SendSmsCodeRequestDto sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder()
                .saId("M15030600001")
                .stbMac("v150.3060.0001")
                .smsCd("S001")
                .ctn("01051603997")
                .replacement("http://google.com/start/we09gn2ks")
                .build();

        Exception exception = assertThrows(NotFoundMsgException.class, () -> {
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

        given(apiClient.smsCallSettingApi(any())).willThrow(new SmsAgentEtcException());
        Exception exception = assertThrows(SmsAgentEtcException.class, () -> {
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
                .codeId("S001")
                .codeName("구매한 VOD를 U+비디오포털앱으로 추가 결제없이 시청하세요. http://goo.gl/YguRj6")
                .build();

        CallSettingResultMapDto resultMapDto = CallSettingResultMapDto.builder()
                .result(CallSettingResultDto.builder()
                        .flag("0000")
                        .message("성공")
                        .totalCount(1)
                        .memberGroup("")
                        .recordset(List.of(dto))
                        .build())
                .build();

        given(apiClient.smsCallSettingApi(any())).willReturn(resultMapDto);
        given(smsAgentDomainClient.send(anyString(), anyString(), anyString())).willThrow(new SystemBusyException());

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

    */

}
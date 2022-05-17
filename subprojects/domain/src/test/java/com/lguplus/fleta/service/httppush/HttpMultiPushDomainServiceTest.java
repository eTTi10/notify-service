package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.*;
import com.lguplus.fleta.properties.HttpServiceProps;
import com.lguplus.fleta.util.HttpPushSupport;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HttpMultiPushDomainServiceTest {

    private static final String SUCCESS_CODE = "200";
    private static final String FAILURE_CODE = "1130";

    @InjectMocks
    HttpMultiPushDomainService httpMultiPushDomainService;

    @Mock
    HttpPushClient httpPushClient;

    @Mock
    HttpPushSupport httpPushSupport;

    @Mock
    HttpServiceProps httpServiceProps;

    @Mock
    Future future;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(httpMultiPushDomainService, "maxMultiCount", 400);
        ReflectionTestUtils.setField(httpMultiPushDomainService, "rejectReg", Set.of("M20110725000", "U01080800201", "U01080800202", "U01080800203"))                                                                                                            ;
    }

    @Test
    @DisplayName("정상적으로 멀티푸시가 성공하는지 확인")
    void whenRequestMultiPush_thenReturnSuccess() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "200");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

    @Test
    @DisplayName("AcceptedException 확인")
    void whenRequestMultiPush_thenThrowAcceptedException() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "202");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("202").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        AcceptedException exception = assertThrows(AcceptedException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(AcceptedException.class);    // AcceptedException 이 발생하였는지 확인
    }

    @Test
    @DisplayName("BadRequestException 확인")
    void whenRequestMultiPush_thenThrowBadRequestException() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "400");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("400").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(BadRequestException.class);    // BadRequestException 이 발생하였는지 확인
    }

    @Test
    @DisplayName("UnAuthorizedException 확인")
    void whenRequestMultiPush_thenThrowUnAuthorizedException() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "401");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("401").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        UnAuthorizedException exception = assertThrows(UnAuthorizedException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(UnAuthorizedException.class);    // UnAuthorizedException 이 발생하였는지 확인
    }

    @Test
    @DisplayName("ForbiddenException 확인")
    void whenRequestMultiPush_thenThrowForbiddenException() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "403");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("403").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(ForbiddenException.class);    // ForbiddenException 이 발생하였는지 확인
    }

    @Test
    @DisplayName("NotFoundException 확인")
    void whenRequestMultiPush_thenThrowNotFoundException() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "404");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("404").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(NotFoundException.class);    // NotFoundException 이 발생하였는지 확인
    }

    @Test
    @DisplayName("유효하지않는 regId 확인")
    void whenRequestMultiPush_thenAckInvalidRegId() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "410");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("410").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

    @Test
    @DisplayName("파라미터 multiCount 가 최대 multiCount 보다 작은지 확인")
    void whenRequestMultiPush_thenAckParameterMultiCountlessThanMaxMultiCount() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "200");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("AAAAAA", "BBBBBB"))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .multiCount(3)
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

    @Test
    @DisplayName("초당 최대 Push 전송 허용 갯수가 최대로 설정되는지 확인")
    void whenRequestMultiPush_thenAckMaxMultiCount() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "200");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("appId")
                .serviceId("1234")
                .pushType("G")
                .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .multiCount(500)
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

    @Test
    @DisplayName("발송제외 가번 확인")
    void whenExclusionNumber_thenThrowExclusionNumberException() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "200");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("M20110725000", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .multiCount(500)
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

    @Test
    @DisplayName("multiCount 확인")
    void whenRequestMultiPush_thenAckMultiCount() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "200");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("11111111", "22222222", "33333333", "44444444", "55555555", "66666666"))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .multiCount(3)
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }

    @Test
    @DisplayName("500 이상 에러 확인")
    void whenRequestMultiPush_thenAckInternalErrorException() {
        // given
        HttpPushCustomException httpPushCustomException = new HttpPushCustomException(500, "1109", "Push GW Internal Error");

        given(httpPushClient.requestHttpPushSingle(anyMap())).willThrow(httpPushCustomException);

        given(httpPushSupport.getHttpServiceProps()).willReturn(httpServiceProps);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1130", "메시지 전송 실패"));

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("11111111", "22222222"))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .multiCount(3)
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(FAILURE_CODE);
    }

    @Test
    @DisplayName("500 미만 에러 확인")
    void whenRequestMultiPush_thenAckNotFoundException() {
        // given
        HttpPushCustomException httpPushCustomException = new HttpPushCustomException(499, "1107", "Push GW Not Found");

        given(httpPushClient.requestHttpPushSingle(anyMap())).willThrow(httpPushCustomException);

        given(httpPushSupport.getHttpServiceProps()).willReturn(httpServiceProps);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1130", "메시지 전송 실패"));

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("11111111", "22222222", "33333333", "44444444", "55555555", "66666666"))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .multiCount(3)
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(FAILURE_CODE);
    }

    @Test
    @DisplayName("Exception 확인")
    void whenRequestMultiPush_thenAckException() {
        // given
        given(httpPushClient.requestHttpPushSingle(anyMap())).willThrow(new AcceptedException());

        given(httpPushSupport.getHttpServiceProps()).willReturn(httpServiceProps);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1130", "메시지 전송 실패"));

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("11111111", "22222222", "33333333", "44444444", "55555555", "66666666"))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .multiCount(3)
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(FAILURE_CODE);
    }

    @Test
    @DisplayName("멀티푸시가 실패시 실패유저가 넘어오는지 확인")
    void whenFailMultiPush_thenReturnFailUsers() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "900");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        given(httpPushSupport.getHttpServiceProps()).willReturn(httpServiceProps);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1130", "메시지 전송 실패"));

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(FAILURE_CODE);   // 실패 코드가 맞는지 확인
    }

    @Test
    @DisplayName("delayTime 함수 테스트")
    void testDelayTime() throws Exception {
        Method method = httpMultiPushDomainService.getClass().getDeclaredMethod("delayTime", Long.TYPE);
        method.setAccessible(true);

        long currentTime = (Long)method.invoke(httpMultiPushDomainService, 1000L);

        assertThat(currentTime).isPositive();
    }

    @Test
    @DisplayName("delayTime 함수 테스트")
    void testDelayTime2() throws Exception {
        Method method = httpMultiPushDomainService.getClass().getDeclaredMethod("delayTime", Long.TYPE);
        method.setAccessible(true);

        long currentTime = (Long)method.invoke(httpMultiPushDomainService, 1000L);

        assertThat(currentTime).isPositive();
    }

    @Test
    @DisplayName("delayTime 함수 호출시 InterruptedException 발생")
    void whenCallDelayTime_thenThrowInterruptedException() {
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

        //Run the code under test
        // given
        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
                .applicationId("lguplushdtvgcm")
                .serviceId("30011")
                .pushType("G")
                .users(List.of("11111111", "22222222", "33333333", "44444444", "55555555", "66666666"))
                .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
                .multiCount(3)
                .build();

        // when
        HttpPushEtcException exception = assertThrows(HttpPushEtcException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(HttpPushEtcException.class);
    }

    @Test
    @DisplayName("responseOpenApi 함수 호출시 InterruptedException 발생")
    void whenCallResponseOpenApi_thenThrowInterruptedException() throws Exception {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<String> calls = () -> {
            Thread.currentThread().interrupt();
            return null;
        };

        final Future<String> future = executorService.submit(calls);

        Method method = httpMultiPushDomainService.getClass().getDeclaredMethod("responseOpenApi", List.class);
        method.setAccessible(true);

        List<Future<String>> futures = List.of(future);
        assertThrows(InvocationTargetException.class, () -> method.invoke(httpMultiPushDomainService, futures));
    }

}
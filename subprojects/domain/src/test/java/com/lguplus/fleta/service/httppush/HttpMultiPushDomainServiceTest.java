package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.OpenApiPushResponseDto;
import com.lguplus.fleta.exception.httppush.AcceptedException;
import com.lguplus.fleta.exception.httppush.BadRequestException;
import com.lguplus.fleta.exception.httppush.ForbiddenException;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.exception.httppush.HttpPushEtcException;
import com.lguplus.fleta.exception.httppush.NotFoundException;
import com.lguplus.fleta.exception.httppush.UnAuthorizedException;
import com.lguplus.fleta.properties.HttpServiceProps;
import com.lguplus.fleta.util.HttpPushSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.lang3.tuple.Pair;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
        ReflectionTestUtils.setField(httpMultiPushDomainService, "rejectReg", Set.of("M20110725000", "U01080800201", "U01080800202", "U01080800203"));
    }

    @Test
    @DisplayName("??????????????? ??????????????? ??????????????? ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // ?????? ????????? ????????? ??????
    }

    @Test
    @DisplayName("AcceptedException ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .build();

        // when
        AcceptedException exception = assertThrows(AcceptedException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(AcceptedException.class);    // AcceptedException ??? ?????????????????? ??????
    }

    @Test
    @DisplayName("BadRequestException ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .build();

        // when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(BadRequestException.class);    // BadRequestException ??? ?????????????????? ??????
    }

    @Test
    @DisplayName("UnAuthorizedException ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .build();

        // when
        UnAuthorizedException exception = assertThrows(UnAuthorizedException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(UnAuthorizedException.class);    // UnAuthorizedException ??? ?????????????????? ??????
    }

    @Test
    @DisplayName("ForbiddenException ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .build();

        // when
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(ForbiddenException.class);    // ForbiddenException ??? ?????????????????? ??????
    }

    @Test
    @DisplayName("NotFoundException ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .build();

        // when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);
        });

        // then
        assertThat(exception).isInstanceOf(NotFoundException.class);    // NotFoundException ??? ?????????????????? ??????
    }

    @Test
    @DisplayName("?????????????????? regId ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // ?????? ????????? ????????? ??????
    }

    @Test
    @DisplayName("???????????? multiCount ??? ?????? multiCount ?????? ????????? ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .multiCount(3)
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // ?????? ????????? ????????? ??????
    }

    @Test
    @DisplayName("?????? ?????? Push ?????? ?????? ????????? ????????? ??????????????? ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .multiCount(500)
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // ?????? ????????? ????????? ??????
    }

    @Test
    @DisplayName("???????????? ?????? ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .multiCount(500)
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // ?????? ????????? ????????? ??????
    }

    @Test
    @DisplayName("multiCount ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .multiCount(3)
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // ?????? ????????? ????????? ??????
    }

    @Test
    @DisplayName("500 ?????? ?????? ??????")
    void whenRequestMultiPush_thenAckInternalErrorException() {
        // given
        HttpPushCustomException httpPushCustomException = new HttpPushCustomException(500, "1109", "Push GW Internal Error");

        given(httpPushClient.requestHttpPushSingle(anyMap())).willThrow(httpPushCustomException);

        given(httpPushSupport.getHttpServiceProps()).willReturn(httpServiceProps);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1130", "????????? ?????? ??????"));

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
            .applicationId("lguplushdtvgcm")
            .serviceId("30011")
            .pushType("G")
            .users(List.of("11111111", "22222222"))
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .multiCount(3)
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(FAILURE_CODE);
    }

    @Test
    @DisplayName("500 ?????? ?????? ??????")
    void whenRequestMultiPush_thenAckNotFoundException() {
        // given
        HttpPushCustomException httpPushCustomException = new HttpPushCustomException(499, "1107", "Push GW Not Found");

        given(httpPushClient.requestHttpPushSingle(anyMap())).willThrow(httpPushCustomException);

        given(httpPushSupport.getHttpServiceProps()).willReturn(httpServiceProps);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1130", "????????? ?????? ??????"));

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
            .applicationId("lguplushdtvgcm")
            .serviceId("30011")
            .pushType("G")
            .users(List.of("11111111", "22222222", "33333333", "44444444", "55555555", "66666666"))
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .multiCount(3)
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(FAILURE_CODE);
    }

    @Test
    @DisplayName("Exception ??????")
    void whenRequestMultiPush_thenAckException() {
        // given
        given(httpPushClient.requestHttpPushSingle(anyMap())).willThrow(new AcceptedException());

        given(httpPushSupport.getHttpServiceProps()).willReturn(httpServiceProps);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1130", "????????? ?????? ??????"));

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
            .applicationId("lguplushdtvgcm")
            .serviceId("30011")
            .pushType("G")
            .users(List.of("11111111", "22222222", "33333333", "44444444", "55555555", "66666666"))
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .multiCount(3)
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(FAILURE_CODE);
    }

    @Test
    @DisplayName("??????????????? ????????? ??????????????? ??????????????? ??????")
    void whenFailMultiPush_thenReturnFailUsers() {
        // given
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("CODE", "900");
        OpenApiPushResponseDto openApiPushResponseDto = OpenApiPushResponseDto.builder().returnCode("200").error(errorMap).build();

        given(httpPushClient.requestHttpPushSingle(anyMap())).willReturn(openApiPushResponseDto);

        given(httpPushSupport.getHttpServiceProps()).willReturn(httpServiceProps);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1130", "????????? ?????? ??????"));

        HttpPushMultiRequestDto httpPushMultiRequestDto = HttpPushMultiRequestDto.builder()
            .applicationId("lguplushdtvgcm")
            .serviceId("30011")
            .pushType("G")
            .users(List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="))
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .build();

        // when
        HttpPushResponseDto responseDto = httpMultiPushDomainService.requestHttpPushMulti(httpPushMultiRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(FAILURE_CODE);   // ?????? ????????? ????????? ??????
    }

    @Test
    @DisplayName("delayTime ?????? ?????????")
    void testDelayTime() throws Exception {
        Method method = httpMultiPushDomainService.getClass().getDeclaredMethod("delayTime", Long.TYPE);
        method.setAccessible(true);

        long currentTime = (Long) method.invoke(httpMultiPushDomainService, 1000L);

        assertThat(currentTime).isPositive();
    }

    @Test
    @DisplayName("delayTime ?????? ?????????")
    void testDelayTime2() throws Exception {
        Method method = httpMultiPushDomainService.getClass().getDeclaredMethod("delayTime", Long.TYPE);
        method.setAccessible(true);

        long currentTime = (Long) method.invoke(httpMultiPushDomainService, 1000L);

        assertThat(currentTime).isPositive();
    }

    @Test
    @DisplayName("delayTime ?????? ????????? InterruptedException ??????")
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
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"?????????\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
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
    @DisplayName("responseOpenApi ?????? ????????? InterruptedException ??????")
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
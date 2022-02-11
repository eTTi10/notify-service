package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.config.HttpPushConfig;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.properties.HttpServiceProps;
import feign.Request;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import fleta.util.JunitTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpPushErrorDecoderTest {

    ErrorDecoder errorDecoder;
    final HttpPushConfig.HttpPushExceptionCode httpPushExceptionCode = new HttpPushConfig.HttpPushExceptionCode();
    final HttpPushConfig.HttpPushExceptionMessage httpPushExceptionMessage = new HttpPushConfig.HttpPushExceptionMessage();
    final Map<String, Collection<String>> headers = new LinkedHashMap<>();
    final Request request = Request.create(Request.HttpMethod.POST, "/test", Collections.emptyMap(), null, Util.UTF_8, null);

    @BeforeEach
    void setUp() {
        Map<String, String> codeMap = new HashMap<>();
        codeMap.put("ExclusionNumberException", "9998");
        codeMap.put("ServiceIdNotFoundException", "1115");
        codeMap.put("AcceptedException", "1112");
        codeMap.put("BadRequestException", "1104");
        codeMap.put("UnAuthorizedException", "1105");
        codeMap.put("ForbiddenException", "1106");
        codeMap.put("NotFoundException", "1107");
        codeMap.put("NotExistRegistIdException", "1113");
        codeMap.put("PreConditionFailedException", "1108");
        codeMap.put("InternalErrorException", "1109");
        codeMap.put("ExceptionOccursException", "1114");
        codeMap.put("ServiceUnavailableException", "1110");

        JunitTestUtils.setValue(httpPushExceptionCode, "httpPush", codeMap);

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("9998", "발송제한번호");
        messageMap.put("1115", "서비스ID 확인 불가");
        messageMap.put("1112", "The request Accepted");
        messageMap.put("1104", "Push GW BadRequest");
        messageMap.put("1105", "Push GW UnAuthorized");
        messageMap.put("1106", "Push GW Forbidden");
        messageMap.put("1107", "Push GW Not Found");
        messageMap.put("1113", "Not Exist RegistID");
        messageMap.put("1108", "Push GW Precondition Failed");
        messageMap.put("1109", "Push GW Internal Error");
        messageMap.put("1114", "Exception Occurs");
        messageMap.put("1110", "Push GW Service Unavailable");

        JunitTestUtils.setValue(httpPushExceptionMessage, "message", messageMap);

        errorDecoder = new HttpPushErrorDecoder(new HttpServiceProps(httpPushExceptionCode, httpPushExceptionMessage));
    }

    @Test
    void testAcceptedException() {
        final Response response = Response.builder()
                .status(202)
                .reason("The request Accepted")
                .request(request)
                .headers(headers)
                .build();

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("The request Accepted");
    }

    @Test
    void testBadRequestException() {
        final Response response = Response.builder()
                .status(400)
                .reason("Push GW BadRequest")
                .request(request)
                .headers(headers)
                .build();

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW BadRequest");
    }

    @Test
    void testUnAuthorizedException() {
        final Response response = Response.builder()
                .status(401)
                .reason("Push GW UnAuthorized")
                .request(request)
                .headers(headers)
                .build();
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW UnAuthorized");
    }

    @Test
    void testForbiddenException() {
        final Response response = Response.builder()
                .status(403)
                .reason("Push GW Forbidden")
                .request(request)
                .headers(headers)
                .build();

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Forbidden");
    }

    @Test
    void testNotFoundException() {
        final Response response = Response.builder()
                .status(404)
                .reason("Push GW Not Found")
                .request(request)
                .headers(headers)
                .build();

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Not Found");
    }

    @Test
    void testNotExistRegisterIdException() {
        final Response response = Response.builder()
                .status(410)
                .reason("Not Exist RegistID")
                .request(request)
                .headers(headers)
                .build();

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Not Exist RegistID");
    }

    @Test
    void testPreConditionFailedException() {
        final Response response = Response.builder()
                .status(412)
                .reason("Push GW Precondition Failed")
                .request(request)
                .headers(headers)
                .build();

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Precondition Failed");
    }

    @Test
    void testInternalErrorException() {
        final Response response = Response.builder()
                .status(500)
                .reason("Push GW Internal Error")
                .request(request)
                .headers(headers)
                .build();

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Internal Error");
    }

    @Test
    void testExceptionOccursException() {
        final Response response = Response.builder()
                .status(502)
                .reason("Exception Occurs")
                .request(request)
                .headers(headers)
                .build();

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Exception Occurs");
    }

    @Test
    void testServiceUnavailableException() {
        final Response response = Response.builder()
                .status(503)
                .reason("Push GW Service Unavailable")
                .request(request)
                .headers(headers)
                .build();

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Service Unavailable");
    }

    @Test
    void testRuntimeException() {
        final Response response = Response.builder()
                .status(9999)
                .reason("기타 오류")
                .request(request)
                .headers(headers)
                .build();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

}
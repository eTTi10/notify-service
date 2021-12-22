package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.exception.httppush.*;
import feign.Request;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import fleta.util.JunitTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpPushErrorDecoderTest {

    final ErrorDecoder errorDecoder = new HttpPushErrorDecoder();
    final Map<String, Collection<String>> headers = new LinkedHashMap<>();
    final Request request = Request.create(Request.HttpMethod.POST, "/test", Collections.emptyMap(), null, Util.UTF_8, null);

    @BeforeEach
    void setUp() {
        JunitTestUtils.setValue(errorDecoder, "acceptedExceptionMsg", "The request Accepted");
        JunitTestUtils.setValue(errorDecoder, "badRequestExceptionMsg", "Push GW BadRequest");
        JunitTestUtils.setValue(errorDecoder, "unAuthorizedExceptionMsg", "Push GW UnAuthorized");
        JunitTestUtils.setValue(errorDecoder, "forbiddenExceptionMsg", "Push GW Forbidden");
        JunitTestUtils.setValue(errorDecoder, "notFoundExceptionMsg", "Push GW Not Found");
        JunitTestUtils.setValue(errorDecoder, "notExistRegistIdExceptionMsg", "Not Exist RegistID");
        JunitTestUtils.setValue(errorDecoder, "preConditionFailedExceptionMsg", "Push GW Precondition Failed");
        JunitTestUtils.setValue(errorDecoder, "internalErrorExceptionMsg", "Push GW Internal Error");
        JunitTestUtils.setValue(errorDecoder, "exceptionOccursExceptionMsg", "Exception Occurs");
        JunitTestUtils.setValue(errorDecoder, "serviceUnavailableExceptionMsg", "Push GW Service Unavailable");
    }

    @Test
    void testAcceptedException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(202)
                    .reason("The request Accepted")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("The request Accepted");
    }

    @Test
    void testBadRequestException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(400)
                    .reason("Push GW BadRequest")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW BadRequest");
    }

    @Test
    void testUnAuthorizedException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(401)
                    .reason("Push GW UnAuthorized")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW UnAuthorized");
    }

    @Test
    void testForbiddenException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(403)
                    .reason("Push GW Forbidden")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Forbidden");
    }

    @Test
    void testNotFoundException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(404)
                    .reason("Push GW Not Found")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Not Found");
    }

    @Test
    void testNotExistRegisterIdException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(410)
                    .reason("Not Exist RegistID")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Not Exist RegistID");
    }

    @Test
    void testPreConditionFailedException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(412)
                    .reason("Push GW Precondition Failed")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Precondition Failed");
    }

    @Test
    void testInternalErrorException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(500)
                    .reason("Push GW Internal Error")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Internal Error");
    }

    @Test
    void testExceptionOccursException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(502)
                    .reason("Exception Occurs")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Exception Occurs");
    }

    @Test
    void testServiceUnavailableException() {
        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            Response response = Response.builder()
                    .status(503)
                    .reason("Push GW Service Unavailable")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception.getMessage()).isSameAs("Push GW Service Unavailable");
    }

    @Test
    void testRuntimeException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            Response response = Response.builder()
                    .status(9999)
                    .reason("기타 오류")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

}
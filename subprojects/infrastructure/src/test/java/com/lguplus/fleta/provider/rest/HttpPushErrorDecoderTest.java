package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.exception.httppush.*;
import feign.Request;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
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

    @Test
    void testAcceptedException() {
        Exception exception = assertThrows(AcceptedException.class, () -> {
            Response response = Response.builder()
                    .status(202)
                    .reason("The request Accepted")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(AcceptedException.class);
    }

    @Test
    void testBadRequestException() {
        Exception exception = assertThrows(BadRequestException.class, () -> {
            Response response = Response.builder()
                    .status(400)
                    .reason("Push GW BadRequest")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(BadRequestException.class);
    }

    @Test
    void testUnAuthorizedException() {
        Exception exception = assertThrows(UnAuthorizedException.class, () -> {
            Response response = Response.builder()
                    .status(401)
                    .reason("Push GW UnAuthorized")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    void testForbiddenException() {
        Exception exception = assertThrows(ForbiddenException.class, () -> {
            Response response = Response.builder()
                    .status(403)
                    .reason("Push GW Forbidden")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void testNotFoundException() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            Response response = Response.builder()
                    .status(404)
                    .reason("Push GW Not Found")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testNotExistRegisterIdException() {
        Exception exception = assertThrows(NotExistRegistIdException.class, () -> {
            Response response = Response.builder()
                    .status(410)
                    .reason("Not Exist RegistID")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(NotExistRegistIdException.class);
    }

    @Test
    void testPreConditionFailedException() {
        Exception exception = assertThrows(PreConditionFailedException.class, () -> {
            Response response = Response.builder()
                    .status(412)
                    .reason("Push GW Precondition Failed")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(PreConditionFailedException.class);
    }

    @Test
    void testInternalErrorException() {
        Exception exception = assertThrows(InternalErrorException.class, () -> {
            Response response = Response.builder()
                    .status(500)
                    .reason("Push GW Internal Error")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(InternalErrorException.class);
    }

    @Test
    void testExceptionOccursException() {
        Exception exception = assertThrows(ExceptionOccursException.class, () -> {
            Response response = Response.builder()
                    .status(502)
                    .reason("Exception Occurs")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(ExceptionOccursException.class);
    }

    @Test
    void testServiceUnavailableException() {
        Exception exception = assertThrows(ServiceUnavailableException.class, () -> {
            Response response = Response.builder()
                    .status(503)
                    .reason("Push GW Service Unavailable")
                    .request(request)
                    .headers(headers)
                    .build();

            throw errorDecoder.decode("HttpPushTest#test()", response);
        });

        assertThat(exception).isInstanceOf(ServiceUnavailableException.class);
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
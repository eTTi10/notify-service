package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.push.*;
import feign.Request;
import feign.Response;
import feign.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PushErrorDecoderTest {

    PushAnnounceDomainClientImpl.PushErrorDecoder pushErrorDecoder;

    NotifyPushRuntimeException thrown;
    final Map<String, Collection<String>> headers = new LinkedHashMap<>();
    final Request request = Request.create(Request.HttpMethod.POST, "/test", Collections.emptyMap(), null, Util.UTF_8, null);

    @BeforeEach
    void setUp() {
        pushErrorDecoder = new PushAnnounceDomainClientImpl.PushErrorDecoder();
    }

    //@Test
    void testServerException() {

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(500)); });
        Assertions.assertTrue(thrown instanceof InternalErrorException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(502)); });
        Assertions.assertTrue(thrown instanceof ExceptionOccursException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(503)); });
        Assertions.assertTrue(thrown instanceof ServiceUnavailableException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(504)); });
        Assertions.assertTrue(thrown instanceof PushEtcException);

    }

    //@Test
    void testClientException() {

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(400)); });
        Assertions.assertTrue(thrown instanceof BadRequestException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(401)); });
        Assertions.assertTrue(thrown instanceof UnAuthorizedException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(403)); });
        Assertions.assertTrue(thrown instanceof ForbiddenException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(404)); });
        Assertions.assertTrue(thrown instanceof NotFoundException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(410)); });
        Assertions.assertTrue(thrown instanceof NotExistRegistIdException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(412)); });
        Assertions.assertTrue(thrown instanceof PreConditionFailedException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(422)); });
        Assertions.assertTrue(thrown instanceof PushEtcException);

    }


    //@Test
    void testFeignException() {

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(202)); });
        Assertions.assertTrue(thrown instanceof AcceptedException);

        thrown = assertThrows(NotifyPushRuntimeException.class, () -> {throw pushErrorDecoder.decode("", getHttpResponse(205)); });
        Assertions.assertTrue(thrown instanceof PushEtcException);

    }

    Response getHttpResponse(int status) {
        return Response.builder()
                .status(status)
                .reason("-")
                .request(request)
                .headers(headers)
                .build();
    }
}
package com.lguplus.fleta.data.type.response;

import com.lguplus.fleta.BootConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {BootConfig.class})
@DisplayName("ResponseCodeType 확인")
class InnerResponseCodeTypeTest {

    private final static String MESSAGE_CODE_PREFIX = "responseCodeType.";
    private final static String MESSAGE_SOURCE_SUFFIX_CODE = ".code";
    private final static String MESSAGE_SOURCE_SUFFIX_MESSAGE = ".message";
    private final static String MESSAGE_SOURCE_SUFFIX_HTTP_STATUS = ".httpStatus";
    private final static String DEFAULT_HTTP_STATUS = "500";

    @Autowired
    MessageSource messageSource;

    private String getMessage(String code, String defaultMessage) {
        return messageSource.getMessage(code, null, defaultMessage, null);
    }

    @Test
    @DisplayName("전체 요소 메시지 확인")
    void allType() {
        for (InnerResponseCodeType type: EnumSet.allOf(InnerResponseCodeType.class)) {
            assertResponseCodeTypeWithMessageSource(type);
        }
    }

    @Test
    @DisplayName("개별 요소 확인 - OK")
    void ok() {
        //given & when
        InnerResponseCodeType type = InnerResponseCodeType.OK;

        //then
        assertThat(type.name()).isEqualTo("OK");
        assertThat(type.code()).isEqualTo("0000");
        assertResponseCodeTypeWithMessageSource(type);
    }

    @Test
    @DisplayName("개별 요소 확인 - BAD_REQUEST")
    void badRequest() {
        //given & when
        InnerResponseCodeType type = InnerResponseCodeType.BAD_REQUEST;

        //then
        assertThat(type.name()).isEqualTo("BAD_REQUEST");
        assertThat(type.code()).isEqualTo("0400");
        assertResponseCodeTypeWithMessageSource(type);
    }

    @Test
    @DisplayName("개별 요소 확인 - INTERNAL_SERVER_ERROR")
    void internalServerError() {
        //given & when
        InnerResponseCodeType type = InnerResponseCodeType.INTERNAL_SERVER_ERROR;

        //then
        assertThat(type.name()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(type.code()).isEqualTo("0500");
        assertResponseCodeTypeWithMessageSource(type);
    }

    private void assertResponseCodeTypeWithMessageSource(InnerResponseCodeType type) {
        String typeName = type.name();
        String codeName = MESSAGE_CODE_PREFIX + typeName + MESSAGE_SOURCE_SUFFIX_CODE;
        String messageName = MESSAGE_CODE_PREFIX + typeName + MESSAGE_SOURCE_SUFFIX_MESSAGE;
        String httpStatusName = MESSAGE_CODE_PREFIX + typeName + MESSAGE_SOURCE_SUFFIX_HTTP_STATUS;
        String code = getMessage(codeName, typeName);
        String message = getMessage(messageName, typeName);
        String httpStatus = getMessage(httpStatusName, DEFAULT_HTTP_STATUS);
        System.out.println("===> " + typeName + " = " + code + " / " + message + " / " + httpStatus);

        assertThat(type.code()).isEqualTo(code);
        assertThat(type.message()).isEqualTo(message);
        assertThat(type.getHttpStatus().value()).isEqualTo(Integer.parseInt(httpStatus));
    }
}
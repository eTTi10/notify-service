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
@DisplayName("ResponseErrorType 확인")
class InnerResponseErrorTypeTest {

    private final static String MESSAGE_SOURCE_PREFIX = "responseErrorType.";
    private final static String MESSAGE_SOURCE_SUFFIX_CODE = ".code";
    private final static String MESSAGE_SOURCE_SUFFIX_MESSAGE = ".message";

    @Autowired
    MessageSource messageSource;

    private String getMessage(String code, String defaultMessage) {
        return messageSource.getMessage(code, null, defaultMessage, null);
    }

    @Test
    @DisplayName("전체 요소 메시지 확인")
    void allType() {
        for (InnerResponseErrorType type: EnumSet.allOf(InnerResponseErrorType.class)) {
            assertResponseErrorTypeWithMessageSource(type);
        }
    }

    @Test
    @DisplayName("개별 요소 확인 - PARAMETER_ERROR")
    void parameterError() {
        //given & when
        InnerResponseErrorType type = InnerResponseErrorType.PARAMETER_ERROR;

        //then
        assertThat(type.name()).isEqualTo("PARAMETER_ERROR");
        assertThat(type.code()).isEqualTo("1400");
        assertResponseErrorTypeWithMessageSource(type);
    }

    private void assertResponseErrorTypeWithMessageSource(InnerResponseErrorType type) {
        String typeName = type.name();
        String codeName = MESSAGE_SOURCE_PREFIX + typeName + MESSAGE_SOURCE_SUFFIX_CODE;
        String messageName = MESSAGE_SOURCE_PREFIX + typeName + MESSAGE_SOURCE_SUFFIX_MESSAGE;
        String code = getMessage(codeName, typeName);
        String message = getMessage(messageName, typeName);
        System.out.println("===> " + typeName + " = " + code + " / " + message);

        assertThat(type.code()).isEqualTo(code);
        assertThat(type.message()).isEqualTo(message);
    }
}
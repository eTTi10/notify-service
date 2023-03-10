package com.lguplus.fleta.data.type.response;

import java.util.EnumSet;
import java.util.Locale;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * HTTP API 표준 응답 코드 (응답 코드, 메시지, HttpStatus는 MessageSource에 정의 : messages/response*.yml)
 *
 * @version 0.1.0
 */
public enum InnerResponseCodeType {
    OK,
    NO_CONTENT,
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    METHOD_NOT_ALLOWED,
    INTERNAL_SERVER_ERROR,
    HTTP_PUSH_SERVER_ERROR,
    PUSH_SERVER_ERROR,
    SMS_SERVER_ERROR,
    MMS_SERVER_ERROR;

    private String code;
    private String message;
    private HttpStatus httpStatus;

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }


    /**
     * ResponseCodeType의 프로퍼티 설정 (MessageSource 이용)
     */
    @Slf4j
    @RequiredArgsConstructor
    @Component
    static class ResponseCodeTypePropertySetter {

        private static final String MESSAGE_CODE_PREFIX = "responseCodeType";
        private static final String DEFAULT_HTTP_STATUS = "500";

        private final MessageSource messageSource;

        @PostConstruct
        void postConstruct() {
            for (InnerResponseCodeType type : EnumSet.allOf(InnerResponseCodeType.class)) {
                String code = getMessage("code", type.name());
                String message = getMessage("message", type.name());
                String httpStatus = getMessage("httpStatus", type.name());
                log.trace("===> MessageSource : {}.{} : {}, {}, {}",
                    MESSAGE_CODE_PREFIX, type.name(), code, message, httpStatus);
                setProperties(type, code, message, httpStatus);
            }
        }

        private void setProperties(InnerResponseCodeType type, String code, String message, String httpStatus) {
            type.code = code;
            type.message = message;
            type.httpStatus = HttpStatus.valueOf(Integer.parseInt(httpStatus));
        }

        private String getMessage(String propName, String name) {
            String msgCode = MESSAGE_CODE_PREFIX + "." + name + "." + propName;
            String defaultMsg = propName.equals("httpStatus") ? DEFAULT_HTTP_STATUS : name;
            return messageSource.getMessage(msgCode, null, defaultMsg, Locale.getDefault());
        }
    }
}

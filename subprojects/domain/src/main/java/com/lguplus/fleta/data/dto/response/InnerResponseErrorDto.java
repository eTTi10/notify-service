package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lguplus.fleta.data.type.response.InnerResponseErrorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * HTTP API 표준 응답 Error 항목
 * @version 0.1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class InnerResponseErrorDto {

    private String code;
    private String message;
    private String detailMessage;

    private InnerResponseErrorDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private InnerResponseErrorDto(String code, String message, String detailMessage) {
        this.code = code;
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public static InnerResponseErrorDto of(InnerResponseErrorType errorType) {
        return new InnerResponseErrorDto(errorType.code(), errorType.message());
    }

    public static InnerResponseErrorDto of(InnerResponseErrorType errorType, String detailMessage) {
        return new InnerResponseErrorDto(errorType.code(), errorType.message(), detailMessage);
    }
}

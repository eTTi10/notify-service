package com.lguplus.fleta.data.vo.error;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lguplus.fleta.data.dto.response.CommonErrorResponseDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(builderMethodName = "errorResponseBuilder")
@JsonPropertyOrder({"code", "message"})
public class ErrorResponseVo implements CommonErrorResponseDto {

    private String flag;
    private String message;

    @Override
    @JsonGetter("code")
    public String getFlag() {
        return flag;
    }

    @Override
    @JsonGetter("message")
    public String getMessage() {
        return message;
    }

    @Override
    public String toPlainText() {
        String columnSep = "!^";
        return String.join(columnSep, getFlag(), getMessage());
    }
}
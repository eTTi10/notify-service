package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class RegistrationIdResponseDto extends SuccessResponseDto{

    @JsonProperty("REG_ID")
    private String regId;

    @Override
    public String toPlainText() {
        return String.join(Separator.COLUMN, super.toPlainText(), regId);
    }
}

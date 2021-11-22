package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


/**
 * REGID response용
 * SuccessResponseDto에 regId라는 특정필드도 response 하기 위해 테스트로 생성
 *
 * */
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

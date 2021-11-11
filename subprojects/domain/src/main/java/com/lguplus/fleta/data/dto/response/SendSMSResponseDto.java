package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SendSMSResponseDto extends SuccessResponseDto {

    private static final long serialVersionUID = 8501001521949716296L;

    @JsonProperty("flag")
    private String mFlag;

    @JsonProperty("message")
    private String mMessage;
}
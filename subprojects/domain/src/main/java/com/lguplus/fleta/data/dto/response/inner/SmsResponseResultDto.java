package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder
public class SmsResponseResultDto implements Serializable {

    @JsonProperty("result")
    private SmsGatewayResponseDto result;

}

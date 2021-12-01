package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder
public class SmsGatewayResponseDto {

    /** 응답코드 */
    @JsonProperty("flag")
    private String flag;

    /** 응답메시지 */
    @JsonProperty("message")
    private String message;

}

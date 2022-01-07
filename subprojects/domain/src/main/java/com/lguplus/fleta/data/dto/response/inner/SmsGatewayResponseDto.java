package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ApiModel(value = "SMS 발송요청 응답결과 DTO", description = "SMS 발송요청 응답결과 DTO")
public class SmsGatewayResponseDto {

    /** 응답코드 */
    @JsonProperty("flag")
    @ApiModelProperty(position = 1, value = "응답코드")
    private String flag;

    /** 응답메시지 */
    @JsonProperty("message")
    @ApiModelProperty(position = 2, value = "응답메시지")
    private String message;

}

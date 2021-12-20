package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JacksonXmlRootElement(localName = "result")
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

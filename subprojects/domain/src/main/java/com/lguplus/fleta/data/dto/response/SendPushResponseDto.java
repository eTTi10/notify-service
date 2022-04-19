package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SuperBuilder
@ToString
@ApiModel(value = "Code를 이용한 푸시 요청 응답결과 DTO", description = "Code를 이용한 푸시 요청 응답결과 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "flag", "message" , "service"})
public class SendPushResponseDto implements CommonResponseDto{

    /** 응답코드 */
    @ApiModelProperty(position = 1, value = "응답코드")
    @JsonProperty(value = "flag")
    private String flag;

    @ApiModelProperty(position = 2, value = "응답메시지")
    @JsonProperty(value = "message")
    private String message;

    @ApiModelProperty(position = 3, value = "서비스별 응답")
    @JsonProperty(value = "service")
    private List<PushServiceResultDto> service;
}

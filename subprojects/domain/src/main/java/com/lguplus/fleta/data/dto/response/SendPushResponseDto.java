package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@ToString
@ApiModel(value = "Code를 이용한 푸시 요청 응답결과 DTO", description = "Code를 이용한 푸시 요청 응답결과 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendPushResponseDto implements CommonResponseDto{

    /** 응답코드 */
    @ApiModelProperty(position = 1, value = "응답코드")    
    private String flag;

    @ApiModelProperty(position = 2, value = "응답메시지")
    private String message;

    @ApiModelProperty(position = 3, value = "서비스별 응답")
    private ArrayList<PushServiceResultDto> service;
}

package com.lguplus.fleta.data.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushServiceResultDto {

    private String sType = "";
    private String sFlag = "";
    private String sMessage = "";
}

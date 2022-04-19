package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CallSettingResultMapDto implements Serializable{

    /** 응답코드 */
    @Builder.Default
    private String code = "200";

    /** 응답메시지 */
    @Builder.Default
    private String message = "성공";

    /** result */
    @JsonProperty("result")
    private CallSettingResultDto result;
}

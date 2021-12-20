package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.swing.text.MaskFormatter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Http Push 에서 사용하는 공통 응답결과 DTO
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "Http Push 응답결과 DTO", description = "Http Push 에서 사용하는 공통 응답결과 DTO")
public class HttpPushResponseDto {

    /** 응답코드 */
    @Builder.Default
    @ApiModelProperty(position = 1, value = "응답코드")
    private String code = "200";

    /** 응답메시지 */
    @Builder.Default
    @ApiModelProperty(position = 2, value = "응답메시지")
    private String message = "Success";

    /** 메시지전송 실패 사용자 */
    @JsonProperty("fail_users")
    @ApiModelProperty(position = 3, value = "메시지전송 실패 사용자")
    private List<String> failUsers;

}

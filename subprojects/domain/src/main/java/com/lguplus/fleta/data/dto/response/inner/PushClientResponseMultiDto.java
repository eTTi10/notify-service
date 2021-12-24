package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * Http Push 에서 사용하는 공통 응답결과 DTO
 *
 */
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonPropertyOrder({"code", "message"})
public class PushClientResponseMultiDto {

    /** 응답코드 */
    @Builder.Default
    @JsonProperty("code")
    private String code = "200";

    /** 응답메시지 */
    @Builder.Default
    @JsonProperty("message")
    private String message = "Success";

    /** 메시지전송 실패 사용자 */
    @JsonProperty("fail_users")
    @ApiModelProperty(position = 3, value = "메시지전송 실패 사용자")
    private List<String> failUsers;

}

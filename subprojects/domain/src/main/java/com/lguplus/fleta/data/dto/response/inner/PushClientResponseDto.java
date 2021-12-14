package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

/**
 * Http Push 에서 사용하는 공통 응답결과 DTO
 *
 */
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"code", "message"})
public class PushClientResponseDto {

    /** 응답코드 */
    @Builder.Default
    @JsonProperty("code")
    private String code = "200";

    /** 응답메시지 */
    @Builder.Default
    @JsonProperty("message")
    private String message = "Success";

    /** 메시지전송 실패 사용자 */
    //@JsonProperty("fail_users")
    //private List<String> failUsers;

}

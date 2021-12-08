package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Push 응답결과 DTO
 *
 */
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
//{status_code=200, push_id=-, status_msg=-, msg_id=-}
public class PushResponseDto {
    @JsonProperty("msg_id")
    private String msgId;

    @JsonProperty("push_id")
    private String pushId;

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("status_msg")
    private String statusMsg;
}

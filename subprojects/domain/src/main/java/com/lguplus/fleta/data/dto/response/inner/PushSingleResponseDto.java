package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Http Push 에서 사용하는 Open API 푸시 호출 공통 응답결과 DTO
 *
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class PushSingleResponseDto {

    /** 응답코드 */
    @JsonProperty("response")
    private ResponseSingle responseData;

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @AllArgsConstructor
    @Builder
    public static class ResponseSingle {
        @JsonProperty("msg_id")
        private String msgId;

        @JsonProperty("push_id")
        private String pushId;

        @JsonProperty("status_code")
        private String statusCode;

        @JsonProperty("status_msg")
        private String statusMsg;
    }

}

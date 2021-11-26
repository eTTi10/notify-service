package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Http Push 에서 사용하는 Open API 푸시 호출 공통 응답결과 DTO
 *
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class PushAnnounceResponseDto {

    /** 응답코드 */
    @JsonProperty("response")
    private ResponseAnnouncement responseAnnouncement;

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    public static class ResponseAnnouncement {
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

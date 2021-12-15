package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Http Push 에서 사용하는 Open API 푸시 호출 공통 응답결과 DTO
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApiPushResponseDto {

    /** 응답코드 */
    @JsonProperty("RETURN_CODE")
    @Builder.Default
    private String returnCode = "200";

    /** Transaction ID (YYYYMMDD+Sequence Number(4byte)) */
    @JsonProperty("PUSH_ID")
    private String pushId;

    /** 응답 시각 (YYYYMMDDhhmmss) */
    @JsonProperty("RESPONSE_TIME")
    private String responseTime;

    /** Push 처리 키 */
    @JsonProperty("PUSH_KEY")
    private String pushKey;

    /** 결과 코드 및 메시지 ({CODE: 코드, MESSAGE: 메시지}) */
    @JsonProperty("ERROR")
    @Builder.Default
    private Map<String, String> error = new HashMap<>();

}

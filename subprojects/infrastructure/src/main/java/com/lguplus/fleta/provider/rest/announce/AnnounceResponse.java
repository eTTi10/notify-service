package com.lguplus.fleta.provider.rest.announce;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class AnnounceResponse {

    ResponseContent response;

    public static class ResponseContent {
        @JsonProperty("msg_id")
        String msgId;
        @JsonProperty("push_id")
        String pushId;
        @JsonProperty("status_code")
        String statusCode;
        @JsonProperty("status_msg")
        String statusMsg;
    }
}

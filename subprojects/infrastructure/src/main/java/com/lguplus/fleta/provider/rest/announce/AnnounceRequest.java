package com.lguplus.fleta.provider.rest.announce;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@Builder
public class AnnounceRequest {

    @JsonProperty("msg_id")
    String msgId;
    @JsonProperty("push_app_id")
    String pushAppId;
    @JsonProperty("noti_type")
    String notiType;
    String pushId;
    String serviceId;
    String servicePasswd;
    String appId;
    String notiContents;

    List<String> notiList;//todo
}

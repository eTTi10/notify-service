package com.lguplus.fleta.data.dto.request.outer;

import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@ToString
public class SendPushCodeRequestDto {

    private String saId;

    private String stbMac;

    private String registrationId;

    private String pushType;

    private String sendCode;

    private String regType;

    private String serviceType;

    private Map<String, String> reserve;

    private List<String> items;

    private String requestBodyStr;

    public String getPushType() {

        if (sendCode.charAt(0) == 'T') {
            pushType = "G";
        }

        return pushType;
    }
}

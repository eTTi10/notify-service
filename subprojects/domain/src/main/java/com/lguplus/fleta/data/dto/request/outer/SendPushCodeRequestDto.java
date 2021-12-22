package com.lguplus.fleta.data.dto.request.outer;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class SendPushCodeRequestDto {

    private String saId;

    private String stbMac;

    private String regId;

    private String pushType;

    private String sendCode;

    private String regType;

    private String serviceType;

    private Map<String, String> reserve;

    private List<String> items;


}

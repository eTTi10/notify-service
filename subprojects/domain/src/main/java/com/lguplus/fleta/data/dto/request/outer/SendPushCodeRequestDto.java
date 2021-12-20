package com.lguplus.fleta.data.dto.request.outer;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;


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

    private String address;

    private String unumber;

    private String reqDate;

    private List<String> items;
}

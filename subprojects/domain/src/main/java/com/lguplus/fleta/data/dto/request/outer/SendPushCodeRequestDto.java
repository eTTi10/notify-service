package com.lguplus.fleta.data.dto.request.outer;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Getter
@ToString
@SuperBuilder
public class SendPushCodeRequestDto {

    private String saId;

    private String stbMac;

    private String regId;

    private String pushType;

    private String sendCode;

    private String regType;

    private String serviceType;

}

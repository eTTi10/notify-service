package com.lguplus.fleta.data.dto.request.inner;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Getter
@ToString
@SuperBuilder
public class PushRequestSingleDto {

    private String appId;
    private String serviceId;
    private String serviceKey;
    private String pushType;
    private String msg;
    private List<String> items;
    private String regId;

}

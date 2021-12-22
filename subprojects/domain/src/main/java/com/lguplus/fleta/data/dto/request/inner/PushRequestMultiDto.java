package com.lguplus.fleta.data.dto.request.inner;

import lombok.*;

import java.util.List;


@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PushRequestMultiDto {

    private String appId;
    private String serviceId;
    private String serviceKey;
    private String pushType;
    private String msg;
    private List<String> items;

    private List<String> users;

}

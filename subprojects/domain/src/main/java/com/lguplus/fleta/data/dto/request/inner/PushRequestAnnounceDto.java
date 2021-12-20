package com.lguplus.fleta.data.dto.request.inner;

import lombok.*;

import java.util.List;


@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PushRequestAnnounceDto {

    private String appId;
    private String serviceId;
    private String pushType;
    private String msg;
    private List<String> items;

}

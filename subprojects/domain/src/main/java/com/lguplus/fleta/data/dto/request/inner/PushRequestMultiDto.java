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

    private String pushType;

    /** 보낼 메시지 */
    private String msg;

    private String multiCount;

    /** 추가할 항목 입력(name!^value) */
    private List<String> items;

    /** 사용자 ID */
    private List<String> users;

}

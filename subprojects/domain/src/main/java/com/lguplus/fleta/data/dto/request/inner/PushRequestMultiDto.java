package com.lguplus.fleta.data.dto.request.inner;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
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

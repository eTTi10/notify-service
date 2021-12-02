package com.lguplus.fleta.data.dto.request.inner;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 공지푸시등록 요청 DTO
 *
 */
@Getter
@Setter
@ToString
@SuperBuilder
public class HttpPushAnnounceRequestDto {

    /** 어플리케이션 ID */
    private String appId;

    /** 서비스 등록시 부여받은 Unique ID */
    private String serviceId;

    /** Push발송 타입 (G: 안드로이드, A: 아이폰) */
    @Builder.Default private String pushType = "G";

    /** 보낼 메시지 */
    private String msg;

    /** 추가할 항목 입력(name!^value) */
    private List<String> items;

}

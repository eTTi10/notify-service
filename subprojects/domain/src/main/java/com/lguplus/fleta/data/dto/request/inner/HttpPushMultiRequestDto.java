package com.lguplus.fleta.data.dto.request.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 멀티푸시등록 요청 DTO
 *
 */
@Getter
@Setter
@ToString
@SuperBuilder
public class HttpPushMultiRequestDto {

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

    /** 사용자 ID */
    private List<String> users;

    /** 초당 최대 Push 전송 허용 갯수  */
    @Builder.Default private Integer multiCount = 0;

}

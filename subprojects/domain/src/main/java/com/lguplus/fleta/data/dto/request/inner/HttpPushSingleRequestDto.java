package com.lguplus.fleta.data.dto.request.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 단건푸시등록 요청 DTO
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class HttpPushSingleRequestDto {

    /** 어플리케이션 ID */
    private String appId;

    /** 서비스 등록시 부여받은 Unique ID */
    private String serviceId;

    /** Push 발송 타입 (G: 안드로이드, A: 아이폰) */
    @Builder.Default private String pushType = "G";

    /** 보낼 메시지 */
    private String msg;

    /** 추가할 항목 입력(name!^value) */
    private List<String> items;

    /** 사용자 ID */
    private List<String> users;

}

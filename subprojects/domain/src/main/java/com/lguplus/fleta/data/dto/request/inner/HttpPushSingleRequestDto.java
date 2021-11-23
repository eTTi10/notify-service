package com.lguplus.fleta.data.dto.request.inner;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Getter
@ToString
@SuperBuilder
public class HttpPushSingleRequestDto {

    private String appId;

    private String serviceId;

    private String pushType;

    /** 보낼 메시지 */
    private String msg;

    /** 추가할 항목 입력(name!^value) */
    private List<String> items;

    /** 사용자 ID */
    private List<String> users;

}

package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.VariationInfoListRequestDto;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@JacksonXmlRootElement(localName = "request")
public class HttpPushSingleRequestVo {

    /** 보낼 메시지 */
    @JacksonXmlProperty(localName="msg")
    private String msg;

    /** 추가할 항목 입력(name!^value) */
    @JacksonXmlElementWrapper(localName="items")
    @JacksonXmlProperty(localName="item")
    private List<T> items;

    /** 사용자 ID */
    @JacksonXmlElementWrapper(localName="users")
    @JacksonXmlProperty(localName="reg_id")
    private List<T> users;



    public HttpPushSingleRequestDto convert() {
        return HttpPushSingleRequestDto.builder()
                .msg(this.getMsg())
                .items(this.getItems())
                .users(this.getUsers())
                .build();
    }

}

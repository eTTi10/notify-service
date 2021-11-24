package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@JacksonXmlRootElement(localName = "request")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PushRequestBodyAnnounceVo {

    /** 보낼 메시지 */
    @NotBlank(message = "필수 BODY DATA 미존재[msg]", payload = ParameterExceedMaxSizeException.class)
    @JacksonXmlProperty(localName="msg")
    private String msg;

    /** 추가할 항목 입력(name!^value) */
    @JacksonXmlElementWrapper(localName="items")
    @JacksonXmlProperty(localName="item")
    private List<String> items;

    public PushRequestAnnounceDto convert(PushRequestParamVo pushRequestParamVo) {
        return PushRequestAnnounceDto.builder()
                .appId(pushRequestParamVo.getAppId())
                .serviceId(pushRequestParamVo.getServiceId())
                .pushType(pushRequestParamVo.getPushType())
                .msg(getMsg())
                .items(getItems())
                .build();
    }

}

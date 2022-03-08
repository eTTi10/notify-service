package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.exception.ParameterMissingException;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@JacksonXmlRootElement(localName = "request")
public class PushAnnounceRequestVo {

    /** 보낼 메시지 */
    @NotBlank(message = "필수 BODY DATA 미존재[msg]", payload = ParameterMissingException.class)
    @JacksonXmlProperty(localName="msg")
    private String message;

    /** 추가할 항목 입력(name!^value) */
    @JacksonXmlElementWrapper(localName="items")
    @JacksonXmlProperty(localName="item")
    private List<String> addItems = new ArrayList<>();

    public PushRequestBodyAnnounceVo convert(String applicationId, String serviceId, String pushType) {

        PushRequestBodyAnnounceVo pushRequestBodyAnnounceVo = new PushRequestBodyAnnounceVo();
        pushRequestBodyAnnounceVo.setApplicationId(applicationId);
        pushRequestBodyAnnounceVo.setServiceId(serviceId);
        pushRequestBodyAnnounceVo.setPushType(pushType);
        pushRequestBodyAnnounceVo.setMessage(getMessage());
        pushRequestBodyAnnounceVo.setAddItems(getAddItems());

        return pushRequestBodyAnnounceVo;
    }

}
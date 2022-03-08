package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.exception.ParameterMissingException;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@JacksonXmlRootElement(localName = "request")
public class PushMultiRequestVo {

    /** 보낼 메시지 */
    @NotBlank(message = "필수 BODY DATA 미존재[msg]", payload = ParameterMissingException.class)
    @JacksonXmlProperty(localName="msg")
    private String message;

    /** 추가할 항목 입력(name!^value) */
    @JacksonXmlElementWrapper(localName="items")
    @JacksonXmlProperty(localName="item")
    private List<String> addItems = new ArrayList<>();

    /** 사용자 List */
    @NotEmpty(message = "필수 BODY DATA 미존재[users]", payload = ParameterMissingException.class)
    @Size(max = 5000, message = "최대 호출횟수 초과")  // 1120
    @JacksonXmlElementWrapper(localName="users")
    @JacksonXmlProperty(localName="reg_id")
    private List<String> users;

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public PushRequestBodyMultiVo convert(String applicationId, String serviceId, String pushType) {

        PushRequestBodyMultiVo pushRequestBodyMultiVo = new PushRequestBodyMultiVo();
        pushRequestBodyMultiVo.setApplicationId(applicationId);
        pushRequestBodyMultiVo.setServiceId(serviceId);
        pushRequestBodyMultiVo.setPushType(pushType);
        pushRequestBodyMultiVo.setMessage(getMessage());
        pushRequestBodyMultiVo.setAddItems(getAddItems());
        pushRequestBodyMultiVo.setUsers(getUsers());

        return pushRequestBodyMultiVo;
    }

}
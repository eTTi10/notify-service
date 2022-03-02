package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@JacksonXmlRootElement(localName = "request")
public class PushSingleRequestVo {

    /** 보낼 메시지 */
    @NotBlank(message = "필수 BODY DATA 미존재[msg]", payload = ParameterExceedMaxSizeException.class)
    @JacksonXmlProperty(localName="msg")
    private String message;

    /** 추가할 항목 입력(name!^value) */
    @JacksonXmlElementWrapper(localName="items")
    @JacksonXmlProperty(localName="item")
    private List<String> addItems = new ArrayList<>();

    /** 사용자 ID */
    @NotBlank(message = "필수 BODY DATA 미존재[reg_id]", payload = ParameterExceedMaxSizeException.class)
    @JacksonXmlProperty(localName="reg_id")
    private String regId;
    /*
    @NotEmpty(message = "필수 BODY DATA 미존재[users]", payload = ParameterExceedMaxSizeException.class)
    @Size(max = 5000, message = "최대 호출횟수 초과")  // 1120
    @JacksonXmlElementWrapper(localName="users")
    @JacksonXmlProperty(localName="reg_id")
    private List<String> users;
    */

    public PushRequestBodySingleVo convert(String applicationId, String serviceId, String pushType) {

        PushRequestBodySingleVo pushRequestBodySingleVo = new PushRequestBodySingleVo();
        pushRequestBodySingleVo.setApplicationId(applicationId);
        pushRequestBodySingleVo.setServiceId(serviceId);
        pushRequestBodySingleVo.setPushType(pushType);
        pushRequestBodySingleVo.setMessage(getMessage());
        pushRequestBodySingleVo.setAddItems(getAddItems());
        pushRequestBodySingleVo.setRegId(getRegId());
        return pushRequestBodySingleVo;
    }

}
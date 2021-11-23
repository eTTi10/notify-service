package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.PushSingleRequestDto;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@JacksonXmlRootElement(localName = "request")
public class PushSingleRequestVo {

    /** 보낼 메시지 */
    @NotBlank(message = "필수 BODY DATA 미존재[msg]", payload = ParameterExceedMaxSizeException.class)
    @JacksonXmlProperty(localName="msg")
    private String msg;

    /** 추가할 항목 입력(name!^value) */
    @JacksonXmlElementWrapper(localName="items")
    @JacksonXmlProperty(localName="item")
    private List<String> items;

    /** 사용자 ID */
    @NotEmpty(message = "필수 BODY DATA 미존재[users]", payload = ParameterExceedMaxSizeException.class)
    @Size(max = 5000, message = "최대 호출횟수 초과")  // 1120
    @JacksonXmlElementWrapper(localName="users")
    @JacksonXmlProperty(localName="reg_id")
    private List<String> users;

    public PushSingleRequestDto convert(PushRequestVo pushRequestVo) {//String appId, String serviceId, String pushType) {
        return PushSingleRequestDto.builder()
                .appId(pushRequestVo.getAppId())
                .serviceId(pushRequestVo.getServiceId())
                .pushType(pushRequestVo.getPushType())
                .msg(getMsg())
                .items(getItems())
                .users(getUsers())
                .build();
    }

}

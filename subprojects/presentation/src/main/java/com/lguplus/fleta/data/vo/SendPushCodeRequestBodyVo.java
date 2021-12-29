package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@JacksonXmlRootElement(localName = "request")
public class SendPushCodeRequestBodyVo {


    @JacksonXmlProperty(localName = "reserve")
    private Map<String, String> reserve;

    @JacksonXmlElementWrapper(localName = "items")
    @JacksonXmlProperty(localName = "item")
    private List<String> items;

    public SendPushCodeRequestDto convert(SendPushCodeRequestVo sendPushCodeRequestVo, String requestBodyStr) {

        return SendPushCodeRequestDto.builder()
                .saId(sendPushCodeRequestVo.getSaId())
                .stbMac(sendPushCodeRequestVo.getStbMac())
                .regId(sendPushCodeRequestVo.getRegId())
                .pushType(sendPushCodeRequestVo.getPushType())
                .sendCode(sendPushCodeRequestVo.getSendCode())
                .regType(sendPushCodeRequestVo.getRegType())
                .serviceType(sendPushCodeRequestVo.getServiceType())
                .reserve(getReserve())
                .items(getItems())
                .requestBodyStr(requestBodyStr)
                .build();
    }

}


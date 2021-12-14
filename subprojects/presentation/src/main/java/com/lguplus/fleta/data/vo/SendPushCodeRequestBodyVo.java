package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@JacksonXmlRootElement(localName = "request")
public class SendPushCodeRequestBodyVo {


    @NotNull(message = "reserve 가 Null 혹은 빈값 입니다.")
    @JacksonXmlProperty(localName = "reserve")
    private Reserve reserve;

    static class Reserve {

        @JacksonXmlProperty(localName = "address")
        private String address;

        @JacksonXmlProperty(localName = "unumber")
        private String unumber;

        @JacksonXmlProperty(localName = "req_date")
        private String reqDate;

    }

    @JacksonXmlElementWrapper(localName = "items")
    @JacksonXmlProperty(localName = "item")
    private List<String> items;

    public SendPushCodeRequestDto convert(SendPushCodeRequestVo sendPushCodeRequestVo) {

        return SendPushCodeRequestDto.builder()
                .saId(sendPushCodeRequestVo.getSaId())
                .stbMac(sendPushCodeRequestVo.getStbMac())
                .regId(sendPushCodeRequestVo.getRegId())
                .pushType(sendPushCodeRequestVo.getPushType())
                .sendCode(sendPushCodeRequestVo.getSendCode())
                .regType(sendPushCodeRequestVo.getRegType())
                .serviceType(sendPushCodeRequestVo.getServiceType())
                .address(reserve.address)
                .unumber(reserve.unumber)
                .reqDate(reserve.reqDate)
                .items(getItems())
                .build();
    }
}


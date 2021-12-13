package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@Getter
@NotBlank(message = "request 가 Null 혹은 빈값 입니다.", groups = Groups.C1.class)
@JacksonXmlRootElement(localName = "request")
public class SendPushCodeRequestBodyVo {

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
                .regType(Optional.ofNullable(sendPushCodeRequestVo.getRegType()).orElse("1"))
                .serviceType(Optional.ofNullable(sendPushCodeRequestVo.getServiceType()).orElse("H"))
                .address(reserve.address)
                .unumber(reserve.unumber)
                .reqDate(reserve.reqDate)
                .items(getItems())
                .build();
    }
}


package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.exception.ParameterContainsNonAlphanumericException;
import com.lguplus.fleta.exception.ParameterContainsWhitespaceException;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.exception.ParameterOverBoundsException;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, SendSMSCodeVo.class})
public class SendPushCodeRequestVo {

    @Pattern(regexp = "^[^\\s]+$", message = "파라미터 sa_id는 값에 공백이 없어야 함", payload = ParameterContainsWhitespaceException.class, groups = Groups.C1.class)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "파라미터 sa_id는 값에 영문,숫자만 포함되어야 함", payload = ParameterContainsNonAlphanumericException.class, groups = Groups.C2.class)
    @Size(min=12, max=12, message = "파라미터 sa_id의 길이는 12 자리 이어야 함", payload = ParameterExceedMaxSizeException.class, groups = Groups.C3.class)
//    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨")
    @ParamAlias("sa_id")
    private String saId;

    @Pattern(regexp = "^([\\w.]+|\\s*)?$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(stb_mac 의 패턴이 일치하지 않습니다.)", groups = Groups.C4.class)
    @Size(max=38, message = "파라미터 stb_mac는 값이 38 이하이어야 함", payload = ParameterOverBoundsException.class, groups = Groups.C5.class)
//    @NotBlank(message = "stb_mac 파라미터값이 전달이 안됨")
    @ParamAlias("stb_mac")
    private String stbMac;

    @ParamAlias("reg_id")
    private String regId;

    @ParamAlias("push_type")
    private String pushType;

    @ParamAlias("send_code")
    private String sendCode;

    @ParamAlias("reg_type")
    private String regType;

    @ParamAlias("service_type")
    private String serviceType;

    public SendPushCodeRequestDto convert() {
        return SendPushCodeRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .pushType(pushType)
                .sendCode(sendCode)
                .regType(regType)
                .serviceType(serviceType)
                .build();
    }

}

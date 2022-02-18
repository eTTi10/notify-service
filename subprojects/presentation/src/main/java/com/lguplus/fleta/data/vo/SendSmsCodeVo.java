package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.exception.ParameterContainsNonAlphanumericException;
import com.lguplus.fleta.exception.ParameterContainsWhitespaceException;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.exception.ParameterOverBoundsException;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, SendSmsCodeVo.class})
public class SendSmsCodeVo {

    @ParamAlias("sa_id")
    private String saId;

    @ParamAlias("stb_mac")
    private String stbMac;

    @NotBlank(message = "필수 요청 정보 누락(sms_cd 가 Null 혹은 빈값 입니다.).", groups = Groups.C6.class)
    @ParamAlias("sms_cd")
    private String smsCd;

    @NotBlank(message = "필수 요청 정보 누락(ctn 가 Null 혹은 빈값 입니다.)", groups = Groups.C7.class)
    @ParamAlias("ctn")
    private String ctn;

    @ParamAlias("replacement")
    private String replacement;
}

package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.outer.SendSMSRequestDto;
import com.lguplus.fleta.exception.ParameterContainsNonAlphanumericException;
import com.lguplus.fleta.exception.ParameterContainsWhitespaceException;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.exception.ParameterOverBoundsException;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class SendSMSVo {

    @NotBlank(message = "s_ctn 필수입니다.")
    @ParamAlias("s_ctn")
    private String sCtn;

    @NotBlank(message = "r_ctn 필수입니다.")
    @ParamAlias("r_ctn")
    private String rCtn;

    @ParamAlias("msg")
    private String msg;

    public SendSMSRequestDto convert(){

        return SendSMSRequestDto.builder()
                .sCtn(getSCtn())
                .rCtn(getRCtn())
                .msg(getMsg())
                .build();
    }

}

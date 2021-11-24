package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

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
                .sCtn(getSCtn().replace("-", "").replace(".", ""))
                .rCtn(getRCtn().replace("-", "").replace(".", ""))
                .msg(getMsg())
                .build();
    }

}

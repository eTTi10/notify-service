package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SendSmsVo {

    @NotBlank(message = "s_ctn 필수입니다.")
    @ParamAlias("s_ctn")
    private String sCtn;

    @NotBlank(message = "r_ctn 필수입니다.")
    @ParamAlias("r_ctn")
    private String rCtn;

    @ParamAlias("msg")
    private String msg;

    public SendSmsRequestDto convert(){

        return SendSmsRequestDto.builder()
                .sCtn(getSCtn().replace("-", "").replace(".", ""))
                .rCtn(getRCtn().replace("-", "").replace(".", ""))
                .msg(getMsg())
                .build();
    }

}

package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.exception.ParameterMissingException;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, SendSmsVo.class})
public class SendSmsVo {

    @NotBlank(message = "s_ctn 파라미터값이 전달이 안됨.", groups = Groups.C1.class)
    @ParamAlias("s_ctn")
    private String sCtn;

    @NotBlank(message = "r_ctn 파라미터값이 전달이 안됨.", groups = Groups.C2.class)
    @ParamAlias("r_ctn")
    private String rCtn;

    @NotBlank(message = "msg 파라미터값이 전달이 안됨.", groups = Groups.C3.class)
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

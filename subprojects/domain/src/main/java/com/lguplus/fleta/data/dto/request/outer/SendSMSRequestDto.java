package com.lguplus.fleta.data.dto.request.outer;

import com.lguplus.fleta.data.dto.request.CommonRequestDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SendSMSRequestDto extends CommonRequestDto {

    private String sCtn;

    private String rCtn;

    private String msg;

}

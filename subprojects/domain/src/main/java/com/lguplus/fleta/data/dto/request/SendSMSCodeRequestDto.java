package com.lguplus.fleta.data.dto.request;

import com.lguplus.fleta.data.dto.request.CommonRequestDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SendSMSCodeRequestDto extends CommonRequestDto {

    private String saId;

    private String stbMac;

    private String smsCd;

    private String ctn;

    private String replacement;
}

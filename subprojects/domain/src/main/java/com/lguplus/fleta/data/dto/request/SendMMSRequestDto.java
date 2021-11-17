package com.lguplus.fleta.data.dto.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SendMMSRequestDto extends CommonRequestDto{

    private String saId;

    private String stbMac;

    private String mmsCd;

    private String ctn;

    private String replacement;

}

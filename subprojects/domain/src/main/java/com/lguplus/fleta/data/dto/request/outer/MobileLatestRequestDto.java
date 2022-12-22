package com.lguplus.fleta.data.dto.request.outer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MobileLatestRequestDto {

    private String saId;

    private String mac;

    private String ctn;

    private String catId;

    private String catName;

    private String regId;

    private String serviceType;
}

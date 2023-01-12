package com.lguplus.fleta.data.dto.request.outer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MobileLatestRequestDto {

    private String saId;

    private String mac;

    private String ctn;

    private String categoryId;

    private String categoryName;

    private String registrantId;

    private String serviceType;
}

package com.lguplus.fleta.data.dto.request.inner;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
public class HttpPushRequestDto {

    private String saId;

    private String serviceType;

    private String sendCode;

    private Map<String, String> reserve;

    private List<String> items;
}

package com.lguplus.fleta.data.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OuterErrorResponseDto implements CommonErrorResponseDto {

    /**
     *
     */
    private final String flag;

    /**
     *
     */
    private final String message;
}
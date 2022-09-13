package com.lguplus.fleta.data.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@Getter
@SuperBuilder
public class ErrorResponseDto implements CommonResponseDto {

    /**
     *
     */
    private final String flag;

    /**
     *
     */
    private final String message;
}

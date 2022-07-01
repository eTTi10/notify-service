package com.lguplus.fleta.data.dto.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@Getter
@SuperBuilder
public class CommonPagingRequestDto extends CommonRequestDto {

    /**
     *
     */
    private Integer startNumber;

    /**
     *
     */
    private Integer requestCount;
}

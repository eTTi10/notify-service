package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RootErrorResponseDto<T extends CommonErrorResponseDto> {

    @JsonProperty
    private T error;
}

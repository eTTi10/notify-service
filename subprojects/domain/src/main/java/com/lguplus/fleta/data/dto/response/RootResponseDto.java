package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RootResponseDto<T extends CommonResponseDto> {

    @JsonProperty
    private T result;
}

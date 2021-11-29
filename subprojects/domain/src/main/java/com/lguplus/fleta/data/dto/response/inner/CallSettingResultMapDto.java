package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@SuperBuilder
public class CallSettingResultMapDto implements Serializable{
    /** result */
    @JsonProperty("result")
    private CallSettingResultDto result;
}

package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.dto.PlainTextibleDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class CallSettingDto implements PlainTextibleDto, Serializable {

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private String value;

    @Override
    public String toPlainText() {
        return String.join(CommonResponseDto.Separator.COLUMN
                , getKey(), getValue());
    }
}

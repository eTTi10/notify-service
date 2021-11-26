package com.lguplus.fleta.data.dto.request.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.dto.PlainTextibleDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class CallSettingRequestDto implements PlainTextibleDto, Serializable {

    @JsonProperty("sa_id")
    private String saId;

    @JsonProperty("stb_mac")
    private String stbMac;

    @JsonProperty("code_id")
    private String codeId;

    @JsonProperty("svc_type")
    private String svcType;

    @Override
    public String toPlainText() {
        return String.join(CommonResponseDto.Separator.COLUMN
                , getSaId(), getStbMac(), getCodeId(), getSvcType());
    }
}

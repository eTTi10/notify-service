package com.lguplus.fleta.data.dto.request.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CallSettingRequestDto implements Serializable {

    @JsonProperty("code")
    private String code;

    @JsonProperty("svc_type")
    private String svcType;

}

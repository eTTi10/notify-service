package com.lguplus.fleta.data.dto.request.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
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

package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CallSettingResultDto implements Serializable{

    /** 응답코드 */
    private String dataType;

    /** 응답메시지 */
    private int dataCount;

    /** 데이터 */
    @JsonProperty("data")
    private CallSettingDto data;
}

package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@SuperBuilder
public class CallSettingResultDto {

    /** 응답코드 */
    @Builder.Default private String flag = "0000";

    /** 응답메시지 */
    @Builder.Default private String message = "성공";

    /** 레코드수 */
    @JsonProperty("total_count")
    @Builder.Default private int totalCount = 0;


    /** 사용자그룹 */
    @JsonProperty("memberGroup")
    private String memberGroup;

    /** 리스트 */
    @JsonProperty("recordset")
    private List<CallSettingDto> recordset;
}

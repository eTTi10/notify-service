package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CallSettingResultDto implements Serializable{

    /** 응답코드 */
    private String flag;

    /** 응답메시지 */
    private String message;

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

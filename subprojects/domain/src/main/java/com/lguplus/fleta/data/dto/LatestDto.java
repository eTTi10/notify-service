package com.lguplus.fleta.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "최신회 알림조회", description = "최신회 알림조회 리턴")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LatestDto implements PlainTextibleDto, Serializable {

    @JsonIgnore
    private String saId;

    @JsonIgnore
    private String mac;

    @JsonIgnore
    private String ctn;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("categoryID") //카테고리 아이디
    private String catId;

    @JsonIgnore
    private String regId;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("categoryName") //카테고리명
    private String catName;

    @JsonIgnore
    private Date rDate;

    @JsonIgnore
    private String categoryGb;

    @Override
    public String toPlainText() {
        return String.join(CommonResponseDto.Separator.COLUMN, getCatId(), getCatName());
    }
}

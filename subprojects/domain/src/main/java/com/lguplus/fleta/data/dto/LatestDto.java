package com.lguplus.fleta.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "최신회 알림조회", description = "최신회 알림조회 리턴")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class LatestDto implements PlainTextibleDto, Serializable {
    @JsonProperty("categoryID") //카테고리 아이디
    private String catId;

    @JsonProperty("categoryName") //카테고리명
    private String catName;

    @Override
    public String toPlainText() {
        return String.join(CommonResponseDto.Separator.COLUMN
                , getCatId(), getCatName());
    }
}

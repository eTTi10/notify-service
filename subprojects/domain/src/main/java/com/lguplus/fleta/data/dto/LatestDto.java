package com.lguplus.fleta.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@ApiModel(value = "최신회 알림조회", description = "최신회 알림조회 리턴")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class LatestDto implements PlainTextibleDto, Serializable {

    @JsonProperty("sa_id")  //가입자 번호
    private String saId;

    @JsonProperty("mac") //가입자 맥주소
    private String mac;

    @JsonProperty("ctn") //전화번호 or 단말 맥 어드레스
    private String ctn;

    @JsonProperty("cat_id") //카테고리 아이디
    private String catId;

    @JsonProperty("reg_id") //Push 할 Reg ID
    private String regId;

    @JsonProperty("cat_name") //카테고리명
    private String catName;

    @JsonProperty("r_date") //등록일시
    private String rDate;

    @JsonProperty("category_gb") //카테고리 구분
    private String categoryGb;

    @Override
    public String toPlainText() {
        return String.join(CommonResponseDto.Separator.COLUMN
                , getSaId(), getMac(), getCtn()
                , getRegId(), getCatId(), getCatName()
                , getRDate(), getCategoryGb());
    }
}

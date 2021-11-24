package com.lguplus.fleta.data.dto.request.outer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lguplus.fleta.data.dto.PlainTextibleDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@SuperBuilder
public class LatestRequestDto implements PlainTextibleDto, Serializable {

    private static final long serialVersionUID = 1L;//무엇????

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

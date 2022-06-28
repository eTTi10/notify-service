package com.lguplus.fleta.data.dto.request.outer;

import com.lguplus.fleta.data.dto.PlainTextibleDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
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
public class LatestRequestDto implements PlainTextibleDto, Serializable {

    private String saId; //가입자 번호

    private String mac; //가입자 맥주소

    private String ctn; //전화번호 or 단말 맥 어드레스

    private String catId; //카테고리 아이디

    private String regId; //Push 할 Reg ID

    private String catName; //카테고리명

    private String rDate; //등록일시

    private String categoryGb; //카테고리 구분

    @Override
    public String toPlainText() {
        return String.join(CommonResponseDto.Separator.COLUMN
            , getSaId(), getMac(), getCtn()
            , getRegId(), getCatId(), getCatName()
            , getRDate(), getCategoryGb());
    }
}

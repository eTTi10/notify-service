package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LatestRequestVo {

    @NotBlank(message = "가입자 번호")
    private String saId;


    @NotBlank(message = "가입자 맥주소")
    private String mac;

    @NotBlank(message = "전화번호 or 단말 맥 어드레스")
    private String ctn;

    @NotBlank(message = "Push 할 Reg ID")
    private String regId;

    @NotBlank(message = "카테고리 아이디")
    private String catId;

    @NotBlank(message = "카테고리명")
    private String catName;

    @NotBlank(message = "등록일시")
    private String rDate;

    @NotBlank(message = "카테고리 구분")
    private String categoryGb;

    public LatestRequestDto convert() {
        return LatestRequestDto.builder()
                .saId(this.getSaId())
                .mac(this.getMac())
                .ctn(this.getCtn())
                .regId(this.getRegId())
                .catId(this.getCatId())
                .catName(this.getCatName())
                .rDate(this.getRDate())
                .categoryGb(this.getCategoryGb())
                .build();
    }
}

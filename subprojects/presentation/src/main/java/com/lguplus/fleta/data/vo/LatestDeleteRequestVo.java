package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, LatestSearchRequestVo.class})
public class LatestDeleteRequestVo {
    @ParamAlias("sa_id")
    @NotBlank(message = "[가입자 번호]가 입력되지 않았습니다.", groups = Groups.C1.class)
    private String saId;

    @ParamAlias("stb_mac")
    @NotBlank(message = "[가입자 맥주소]가 입력되지 않았습니다.", groups = Groups.C2.class)
    private String mac;

    @ParamAlias("ctn")
    @NotBlank(message = "[전화번호 or 단말 맥 어드레스]가 입력되지 않았습니다.", groups = Groups.C3.class)
    //숫자판별 @Positive(message = "[전화번호 or 단말 맥 어드레스]의 입력형식이 올바르지 않습니다.")
    private String ctn;

    @ParamAlias("cat_id")
    @NotBlank(message = "[카테고리 아이디]가 입력되지 않았습니다.", groups = Groups.C4.class)
    private String catId;

    public LatestRequestDto convert() {
        return LatestRequestDto.builder()
                .saId(this.getSaId())
                .mac(this.getMac())
                .ctn(this.getCtn())
                .catId(this.getCatId())
                .build();
    }
}

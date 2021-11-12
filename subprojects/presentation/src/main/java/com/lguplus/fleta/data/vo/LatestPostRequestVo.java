package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;


import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, LatestSearchRequestVo.class})
public class LatestPostRequestVo {
    @ParamAlias("sa_id")
    @NotBlank(message = "[가입자 번호]가 입력되지 않았습니다.", groups = Groups.C1.class)
    private String saId;

    @ParamAlias("stb_mac")
    @NotBlank(message = "[가입자 맥주소]가 입력되지 않았습니다.", groups = Groups.C2.class)
    private String mac;

    @ParamAlias("ctn")
    @NotBlank(message = "[전화번호 or 단말 맥 어드레스]가 입력되지 않았습니다.", groups = Groups.C3.class)
    @Positive(message = "[전화번호 or 단말 맥 어드레스]의 입력형식이 올바르지 않습니다.", groups = Groups.C4.class)
    private String ctn;

    @ParamAlias("cat_id")
    @NotBlank(message = "[카테고리 아이디]가 입력되지 않았습니다.", groups = Groups.C5.class)
    private String catId;

    @ParamAlias("reg_id") //Push 할 Reg ID
    @NotBlank(message = "[Reg ID]가 입력되지 않았습니다.", groups = Groups.C6.class)
    private String regId;

    @ParamAlias("cat_name") //카테고리명
    @NotBlank(message = "[카테고리명]가 입력되지 않았습니다.", groups = Groups.C7.class)
    private String catName;

    @ParamAlias("category_gb") //카테고리 구분
    //@Value("I20") <-- 적용이 되지 않았음
    //@NotBlank(message = "[카테고리 구분]가 입력되지 않았습니다.")
    private String categoryGb;

    public String getCategoryGb() {
        if(StringUtils.isEmpty(categoryGb) || categoryGb == null || "null".equals(categoryGb)){
            return "I20";
        }
        return categoryGb;
    }

    //LatestRequestDto
    public LatestRequestDto convert() {
        return LatestRequestDto.builder()
                .saId(this.getSaId())
                .mac(this.getMac())
                .ctn(this.getCtn())
                .catId(this.getCatId())
                .regId(this.getRegId())
                .catName(this.getCatName())
                .categoryGb(this.getCategoryGb())
                .build();
    }
}

package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.validation.Groups;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, LatestPostRequestVo.class})
public class LatestPostRequestVo {


    @ParamAlias("sa_id")
    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "sa_id의 패턴이 일치하지 않습니다.)", groups = Groups.C7.class)
    private String saId;

    @ParamAlias("stb_mac")
    @NotBlank(message = "[가입자 맥주소]가 입력되지 않았습니다.", groups = Groups.C2.class)
    @Pattern(regexp = "^[a-zA-Z0-9.]*$", message = "stb_mac의 패턴이 일치하지 않습니다.)", groups = Groups.C8.class)
    private String mac;

    @ParamAlias("ctn")
    @NotBlank(message = "[전화번호 or 단말 맥 어드레스]가 입력되지 않았습니다.", groups = Groups.C3.class)
    //{ 양수 또는 0} 허용
    @PositiveOrZero(message = "[전화번호 or 단말 맥 어드레스]의 입력형식이 올바르지 않습니다.", groups = Groups.C4.class)
    private String ctn;

    @ParamAlias("cat_id")
    @NotBlank(message = "[카테고리 아이디]가 입력되지 않았습니다.", groups = Groups.C4.class)
    private String catId;

    @ParamAlias("reg_id") //Push 할 Reg ID
    @NotBlank(message = "[Reg ID]가 입력되지 않았습니다.", groups = Groups.C5.class)
    private String regId;

    @ParamAlias("cat_name") //카테고리명
    @NotBlank(message = "[카테고리명]가 입력되지 않았습니다.", groups = Groups.C6.class)
    private String catName;

    @ParamAlias("category_gb") //카테고리 구분
    private String categoryGb;
    public String getCategoryGb() {
        if(StringUtils.isEmpty(categoryGb)){
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

package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.exception.InvalidRequestTypeException;
import com.lguplus.fleta.validation.Groups;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, LatestPostRequestVo.class})
public class LatestPostRequestVo {


    @ParamAlias("sa_id")
    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)
    private String saId;

    @ParamAlias("stb_mac")
    @NotBlank(message = "stb_mac 파라미터값이 전달이 안됨", groups = Groups.C2.class)
    private String mac;

    @ParamAlias("ctn")
    @NotBlank(message = "ctn 파라미터값이 전달이 안됨", groups = Groups.C3.class)
    @PositiveOrZero(message = "ctn 파라미터는 숫자형 데이터이어야 함", payload = InvalidRequestTypeException.class, groups = Groups.C4.class)
    private String ctn;

    @ParamAlias("cat_id")
    @NotBlank(message = "cat_id 파라미터값이 전달이 안됨", groups = Groups.C4.class)
    private String catId;

    @ParamAlias("cat_name") //카테고리명
    @NotBlank(message = "cat_name 파라미터값이 전달이 안됨", groups = Groups.C5.class)
    private String catName;

    @ParamAlias("reg_id") //Push 할 Reg ID
    @NotBlank(message = "reg_id 파라미터값이 전달이 안됨", groups = Groups.C6.class)
    private String regId;

    @ParamAlias("category_gb") //카테고리 구분
    private String categoryGb;

    public String getCategoryGb() {
        if (StringUtils.isBlank(categoryGb)) {
            return "I20";
        }
        return categoryGb;
    }

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

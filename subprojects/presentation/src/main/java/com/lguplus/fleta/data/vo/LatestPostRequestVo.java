package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.exception.InvalidRequestTypeException;
import com.lguplus.fleta.exception.ParameterDatabaseException;
import com.lguplus.fleta.validation.Groups;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, LatestPostRequestVo.class})
public class LatestPostRequestVo {


    @ParamAlias("sa_id")
    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "DB 에러", payload = ParameterDatabaseException.class, groups = Groups.C7.class)
    @Size(max = 12, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String saId;

    @ParamAlias("stb_mac")
    @NotBlank(message = "stb_mac 파라미터값이 전달이 안됨", groups = Groups.C2.class)
    @Pattern(regexp = "^[a-zA-Z0-9.]*$", message = "stb_mac의 패턴이 일치하지 않음", groups = Groups.C8.class)
    @Size(max = 14, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String mac;

    @ParamAlias("ctn")
    @NotBlank(message = "ctn 파라미터값이 전달이 안됨", groups = Groups.C3.class)
    //{ 양수 또는 0} 허용
    @PositiveOrZero(message = "ctn 파라미터는 숫자형 데이터이어야 함", payload = InvalidRequestTypeException.class, groups = Groups.C4.class)
    @Size(max = 11, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String ctn;

    @ParamAlias("cat_id")
    @NotBlank(message = "cat_id 파라미터값이 전달이 안됨", groups = Groups.C4.class)
    @Size(max = 5, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String catId;

    @ParamAlias("reg_id") //Push 할 Reg ID
    @NotBlank(message = "reg_id 파라미터값이 전달이 안됨", groups = Groups.C5.class)
    @Size(max = 64, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String regId;

    @ParamAlias("cat_name") //카테고리명
    @NotBlank(message = "cat_name 파라미터값이 전달이 안됨", groups = Groups.C6.class)
    @Size(max = 200, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String catName;

    @ParamAlias("category_gb") //카테고리 구분
    @Size(max = 3, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String categoryGb;

    public String getCategoryGb() {
        if (StringUtils.isEmpty(categoryGb)) {
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

package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import com.lguplus.fleta.exception.ParameterLengthOverLimitException;
import com.lguplus.fleta.exception.ParameterTypeMismatchException;
import com.lguplus.fleta.validation.Groups;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@GroupSequence({Groups.R1.class, Groups.R2.class, Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class
    , Groups.C6.class, Groups.C7.class, Groups.C8.class, Groups.C9.class, Groups.C10.class, MobileLatestDeleteRequestVo.class})
public class MobileLatestDeleteRequestVo {

    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨", groups = Groups.R1.class)
    @Size(max = 12, message = "요청 정보 허용 범위 초과(sa_id - 12 자리 이하)", payload = ParameterLengthOverLimitException.class, groups = Groups.C1.class)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(sa_id - 특수문자 입력 불가)", payload = ParameterTypeMismatchException.class, groups = Groups.C2.class)
    @Pattern(regexp = "^[^\\s]+$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(sa_id - 공백 입력 불가)", payload = ParameterTypeMismatchException.class, groups = Groups.C3.class)
    @ParamAlias("sa_id")
    private String saId;

    @NotBlank(message = "stb_mac 파라미터값이 전달이 안됨", groups = Groups.R2.class)
    @Size(max = 15, message = "요청 정보 허용 범위 초과(stb_mac - 15 자리 이하)", payload = ParameterLengthOverLimitException.class, groups = Groups.C4.class)
    @Pattern(regexp = "^[a-zA-Z0-9.]*$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(stb_mac - 특수문자 입력 불가)", payload = ParameterTypeMismatchException.class, groups = Groups.C5.class)
    @Pattern(regexp = "^[^\\s]+$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(stb_mac - 공백 입력 불가)", payload = ParameterTypeMismatchException.class, groups = Groups.C6.class)
    @ParamAlias("stb_mac")
    private String mac;

    @NotBlank(message = "ctn 파라미터값이 전달이 안됨", groups = Groups.C7.class)
    @ParamAlias("ctn")
    private String ctn;

    @NotBlank(message = "cat_id 파라미터값이 전달이 안됨", groups = Groups.C8.class)
    @Size(max = 5, message = "요청 정보 허용 범위 초과(cat_id - 5 자리 이하)", payload = ParameterLengthOverLimitException.class, groups = Groups.C9.class)
    @Size(min = 2, message = "요청 정보 허용 범위 초과(cat_id - 1자리 입력 불가)", payload = ParameterLengthOverLimitException.class, groups = Groups.C10.class)
    @ParamAlias("cat_id")
    private String categoryId;

    @ParamAlias("service_type")
    private String serviceType;

    public String getServiceType() {
        return StringUtils.defaultIfBlank(serviceType, "V").toUpperCase();
    }

    public MobileLatestRequestDto convert() {
        return MobileLatestRequestDto.builder()
            .saId(this.getSaId())
            .mac(this.getMac())
            .ctn(this.getCtn())
            .categoryId(this.getCategoryId())
            .serviceType(this.getServiceType())
            .build();
    }
}

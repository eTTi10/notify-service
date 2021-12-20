package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.exception.ParameterContainsNonAlphanumericException;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.exception.ParameterOverBoundsException;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@GroupSequence({SendPushCodeRequestVo.class})
public class SendPushCodeRequestVo {

    @NotBlank(message = "sa_id 가 Null 혹은 빈값 입니다.", groups = Groups.C1.class)
    @Size(min=8, message = "sa_id 의 길이가 8 보다 작습니다.", payload = ParameterExceedMaxSizeException.class, groups = Groups.C2.class)
    @Size(max=15, message = "sa_id 의 길이가 15 보다 큽니다.", payload = ParameterExceedMaxSizeException.class, groups = Groups.C3.class)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "파라미터 sa_id 는 영문+숫자만 가능합니다", payload = ParameterContainsNonAlphanumericException.class, groups = Groups.C4.class)
//    @Pattern(regexp = "^[^\\s]+$", message = "파라미터 sa_id는 값에 공백이 없어야 함", payload = ParameterContainsWhitespaceException.class, groups = Groups.C1.class)
    @ParamAlias("sa_id")
    private String saId;

    @NotBlank(message = "stb_mac 가 Null 혹은 빈값 입니다.", groups = Groups.C5.class)
    @Size(min=10, message = "stb_mac 의 길이가 10 보다 작습니다.", payload = ParameterExceedMaxSizeException.class, groups = Groups.C6.class)
    @Size(max=20, message = "stb_mac 의 길이가 20 보다 큽니다.", payload = ParameterExceedMaxSizeException.class, groups = Groups.C7.class)
    @Pattern(regexp = "^[\\w\\.]+$", message = "stb_mac 의 패턴이 일치하지 않습니다.", groups = Groups.C8.class) //AS IS 패턴
//    @Pattern(regexp = "^([\\w.]+|\\s*)?$", message = "stb_mac 의 패턴이 일치하지 않습니다.", groups = Groups.C4.class)
    @ParamAlias("stb_mac")
    private String stbMac;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "reg_id 는 영문+숫자만 가능합니다", payload = ParameterContainsNonAlphanumericException.class, groups = Groups.C9.class)
    @NotBlank(message = "reg_id 가 Null 혹은 빈값 입니다.", groups = Groups.C10.class)
    @ParamAlias("reg_id")
    private String regId;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "send_code 는 영문+숫자만 가능합니다", payload = ParameterContainsNonAlphanumericException.class, groups = Groups.C11.class)
    @NotBlank(message = "send_code 가 Null 혹은 빈값 입니다.", groups = Groups.C12.class)
    @ParamAlias("send_code")
    private String sendCode;

    @Size(max=5, message = "push_type 의 길이가 5 보다 큽니다.", payload = ParameterOverBoundsException.class, groups = Groups.C13.class)
    @Pattern(regexp = "^[AGL\\|]*$", message = "stb_mac 의 패턴이 일치하지 않습니다.", groups = Groups.C14.class)
    @NotBlank(message = "push_type 가 Null 혹은 빈값 입니다.", groups = Groups.C15.class)
    @ParamAlias("push_type")
    private String pushType;

    @Size(max=1, message = "reg_type 의 길이가 1 보다 큽니다.", payload = ParameterOverBoundsException.class, groups = Groups.C16.class)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "reg_type 는 영문+숫자만 가능합니다", payload = ParameterContainsNonAlphanumericException.class, groups = Groups.C17.class)
    @ParamAlias("reg_type")
    private String regType;

    @ParamAlias("service_type")
    private String serviceType;

    public String getRegType() {
        return StringUtils.defaultIfEmpty(regType,"1");
    }

    public String getServiceType() {
        return StringUtils.defaultIfEmpty(serviceType,"H");
    }

}

package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.exception.*;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, Groups.C8.class, Groups.C9.class, Groups.C10.class, Groups.C11.class, Groups.C12.class, Groups.C13.class, Groups.C14.class, Groups.C15.class, Groups.C16.class, Groups.C17.class, SendPushCodeRequestVo.class})
public class SendPushCodeRequestVo {

    @NotBlank(message = "필수 요청 정보 누락(sa_id 가 Null 혹은 빈값 입니다.)", groups = Groups.C1.class)
    @Size(min=8, message = "요청 정보 허용 범위 미만(sa_id 의 길이가 8 보다 작습니다.)", payload = ParameterLengthUnderLimitException.class, groups = Groups.C2.class)
    @Size(max=15, message = "요청 정보 허용 범위 초과(sa_id 의 길이가 15 보다 큽니다.)", payload = ParameterLengthOverLimitException.class, groups = Groups.C3.class)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(sa_id 는 영문+숫자만 가능합니다)", payload = ParameterTypeMismatchException.class, groups = Groups.C4.class)
    @ParamAlias("sa_id")
    private String saId;

    @NotBlank(message = "필수 요청 정보 누락(stb_mac 가 Null 혹은 빈값 입니다.)", groups = Groups.C5.class)
    @Size(min=10, message = "요청 정보 허용 범위 미만(stb_mac 의 길이가 10 보다 작습니다.)", payload = ParameterLengthUnderLimitException.class, groups = Groups.C6.class)
    @Size(max=20, message = "요청 정보 허용 범위 초과(stb_mac 의 길이가 20 보다 큽니다.)", payload = ParameterLengthOverLimitException.class, groups = Groups.C7.class)
    @Pattern(regexp = "^[\\w\\.]+$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(stb_mac 의 패턴이 일치하지 않습니다.)", groups = Groups.C8.class) //AS IS 패턴
    @ParamAlias("stb_mac")
    private String stbMac;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(reg_id 는 영문+숫자만 가능합니다)", payload = ParameterTypeMismatchException.class, groups = Groups.C9.class)
    @NotBlank(message = "필수 요청 정보 누락(reg_id 가 Null 혹은 빈값 입니다.)", groups = Groups.C10.class)
    @ParamAlias("reg_id")
    private String regId;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(send_code 는 영문+숫자만 가능합니다)", payload = ParameterTypeMismatchException.class, groups = Groups.C11.class)
    @NotBlank(message = "필수 요청 정보 누락(send_code 가 Null 혹은 빈값 입니다.)", groups = Groups.C12.class)
    @ParamAlias("send_code")
    private String sendCode;

    @Size(max=5, message = "요청 정보 허용 범위 초과(push_type 의 길이가 5 보다 큽니다.)", payload = ParameterLengthOverLimitException.class, groups = Groups.C13.class)
    @Pattern(regexp = "^[AGL\\|]*$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(push_type 의 패턴이 일치하지 않습니다.)", groups = Groups.C14.class)
    @NotBlank(message = "필수 요청 정보 누락(push_type 가 Null 혹은 빈값 입니다.)", groups = Groups.C15.class)
    @ParamAlias("push_type")
    private String pushType;

    @Size(max=1, message = "요청 정보 허용 범위 초과(reg_type 의 길이가 1 보다 큽니다.)", payload = ParameterLengthOverLimitException.class, groups = Groups.C16.class)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(reg_type 는 영문+숫자만 가능합니다)", payload = ParameterTypeMismatchException.class, groups = Groups.C17.class)
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

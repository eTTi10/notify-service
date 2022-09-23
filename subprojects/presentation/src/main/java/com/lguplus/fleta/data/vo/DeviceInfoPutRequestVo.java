package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.exception.*;
import com.lguplus.fleta.validation.AlphabetAndNumberPattern;
import com.lguplus.fleta.validation.Groups;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class,Groups.C3.class, Groups.C4.class, Groups.C5.class,
        Groups.C6.class, Groups.C7.class, Groups.C8.class, Groups.C9.class, Groups.C10.class,
        Groups.C11.class, Groups.C12.class, Groups.C13.class, Groups.C14.class,Groups.C15.class,
        Groups.C16.class ,Groups.C17.class,Groups.C18.class,DeviceInfoPutRequestVo.class})
@ApiModel(value = "단말 정보 요청 VO", description = "단말 정보 요청 VO")
public class DeviceInfoPutRequestVo{

    @ParamAlias("sa_id")
    @NotBlank(message = "필수 요청 정보 누락(sa_id)",groups = Groups.C2.class)
    @AlphabetAndNumberPattern(message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(sa_id - 특수문자 입력 불가)",  groups = Groups.C3.class, payload = ParameterTypeMismatchException.class)
    @Size(max=12, message = "요청 정보 허용 범위 초과(sa_id - 12 자리 이하)" ,payload = ParameterLengthOverLimitException.class, groups = Groups.C4.class)
    private String saId;

    @ParamAlias("service_type")
    @NotBlank(message = "필수 요청 정보 누락(service_type)",groups = Groups.C6.class)
    @Pattern(regexp = "[HUCRGDBK]", message = "파라미터 service_type는 값의 범위가 H|U|C|R|G|D|B 이어야 함",groups = Groups.C8.class,payload = ParameterOutOfRangeException.class)
    private String serviceType;

    @ParamAlias("agent_type")
    @NotBlank(message = "필수 요청 정보 누락(agent_type)",groups = Groups.C9.class)
    @Pattern(regexp = "[GA]", message = "파라미터 agent_type는 값의 범위가 G|A 이어야 함",groups = Groups.C11.class,payload = ParameterOutOfRangeException.class)
    private String agentType;

    @ParamAlias("noti_type")
    @NotBlank(message = "필수 요청 정보 누락(noti_type)",groups = Groups.C12.class)
    @Pattern(regexp = "[ASN]", message = "파라미터 noti_type는 값의 범위가 A|S|N 이어야 함",groups = Groups.C15.class,payload = ParameterOutOfRangeException.class)
    private String notiType;

    @ParamAlias("stb_mac")
    @NotBlank(message = "필수 요청 정보 누락(stb_mac)",groups = Groups.C16.class)
    @AlphabetAndNumberPattern(message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(stb_mac - 특수문자 입력 불가)",  groups = Groups.C17.class, payload = ParameterTypeMismatchException.class)
    @Size(max=38, message = "요청 정보 허용 범위 초과(stb_mac - 38 자리 이하)" ,payload = ParameterLengthOverLimitException.class, groups = Groups.C18.class)
    private String stbMac;

    private String accessKey;
    private String cpId;
    public DeviceInfoRequestDto convert(){
        return DeviceInfoRequestDto.builder()
                .saId(this.getSaId())
                .serviceType(this.getServiceType())
                .agentType(this.getAgentType())
                .notiType(this.getNotiType())
                .build();
    }
}

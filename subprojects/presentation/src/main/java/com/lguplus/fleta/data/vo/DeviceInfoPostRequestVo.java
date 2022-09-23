package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.exception.*;
import com.lguplus.fleta.validation.Groups;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class,Groups.C3.class, Groups.C4.class, Groups.C5.class,
        Groups.C6.class, Groups.C7.class, Groups.C8.class, Groups.C9.class, Groups.C10.class,
        Groups.C11.class, Groups.C12.class, Groups.C13.class, Groups.C14.class,Groups.C15.class,
        Groups.C16.class ,Groups.C17.class,Groups.C18.class,DeviceInfoPostRequestVo.class})
@ApiModel(value = "단말 정보 요청 VO", description = "단말 정보 요청 VO")
public class DeviceInfoPostRequestVo{

    @ParamAlias("sa_id")
    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨",groups = Groups.C2.class)
    private String saId;

    @ParamAlias("service_type")
    @NotBlank(message = "service_type 파라미터값이 전달이 안됨",groups = Groups.C6.class)
    private String serviceType;

    @ParamAlias("agent_type")
    @NotBlank(message = "agent_type 파라미터값이 전달이 안됨",groups = Groups.C9.class)
    private String agentType;

    @ParamAlias("noti_type")
    @NotBlank(message = "noti_type 파라미터값이 전달이 안됨",groups = Groups.C12.class)
    @Pattern(regexp = "[ASN]", message = "파라미터 noti_type는 값의 범위가 A|S|N 이어야 함",groups = Groups.C14.class,payload = ParameterOutOfRangeException.class)
    private String notiType;

    private String accessKey;
    private String cpId;
    private String stbMac;
    public DeviceInfoRequestDto convert(){
        return DeviceInfoRequestDto.builder()
                .saId(this.getSaId())
                .serviceType(this.getServiceType())
                .agentType(this.getAgentType())
                .notiType(this.getNotiType())
                .build();
    }
}

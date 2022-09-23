package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.validation.AlphabetAndNumberOrEmptyPattern;
import com.lguplus.fleta.validation.Groups;
import io.swagger.annotations.ApiModel;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(value = "단말 정보 요청 VO", description = "단말 정보 요청 VO")
public class DeviceInfoRequestVo {

    @ParamAlias("sa_id")
    @NotBlank(groups = {Groups.C1.class, Groups.C2.class})
    @AlphabetAndNumberOrEmptyPattern(groups = {Groups.C1.class, Groups.C2.class})
    @Size(max=12, message = "sa_id는 12자리 이하 입니다.",groups = {Groups.C1.class, Groups.C2.class})
    private String saId;

    @ParamAlias("service_type")
    @NotBlank(groups = {Groups.C1.class, Groups.C2.class})
    @AlphabetAndNumberOrEmptyPattern(groups = {Groups.C1.class, Groups.C2.class})
    @Pattern(regexp = "[HUCRGDB]", message = "허용되지 않은 service_type 값입니다.",groups = {Groups.C1.class, Groups.C2.class})
    private String serviceType;

    @ParamAlias("agent_type")
    @NotBlank(groups = {Groups.C1.class, Groups.C2.class})
    @AlphabetAndNumberOrEmptyPattern(groups = {Groups.C1.class, Groups.C2.class})
    @Pattern(regexp = "[GA]", message = "허용되지 않은 agent_type 값입니다.",groups = {Groups.C1.class, Groups.C2.class})
    private String agentType;

    @ParamAlias("noti_type")
    @NotBlank(groups = {Groups.C1.class})
    @Pattern(regexp = "[ASN]", message = "허용되지 않은 noti_type 값입니다.",groups = {Groups.C1.class})
    private String notiType;

    private String accessKey;
    private String cpId;
    private String stbMac;
}

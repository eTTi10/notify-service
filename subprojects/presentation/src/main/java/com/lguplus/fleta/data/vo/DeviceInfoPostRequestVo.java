package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.validation.AlphabetAndNumberOrEmptyPattern;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeviceInfoPostRequestVo {
    @ParamAlias("sa_id")
    @AlphabetAndNumberOrEmptyPattern
    @Size(max=12, message = "sa_id는 12자리 이하 입니다.")
    private String saId;

    @ParamAlias("service_type")
    @AlphabetAndNumberOrEmptyPattern
    @Pattern(regexp = "[HUCRGDB]", message = "허용되지 않은 service_type 값입니다.")
    private String serviceType;

    @ParamAlias("agent_type")
    @AlphabetAndNumberOrEmptyPattern
    @Pattern(regexp = "[GA]", message = "허용되지 않은 agent_type 값입니다.")
    private String agentType;

    @NotBlank
    @Pattern(regexp = "[ASN]", message = "허용되지 않은 noti_type 값입니다.")
    @ParamAlias("noti_type")
    private String notiType;

    private String accessKey;
    private String cpId;
    private String stbMac;
}

package com.lguplus.fleta.data.vo;


import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.exception.ParameterDatabaseException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DeviceInfoPostRequestVo {
    @ParamAlias("sa_id")
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "입력은 A-Z,a-z,0-9 범위 입니다", payload = ParameterDatabaseException.class)
    @Size(max=12, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String saId;

    @ParamAlias("service_type")
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "입력은 A-Z,a-z,0-9 범위 입니다", payload = ParameterDatabaseException.class)
    @Size(max=12, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String serviceType;

    @ParamAlias("agent_type")
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "입력은 A-Z,a-z,0-9 범위 입니다", payload = ParameterDatabaseException.class)
    @Size(max=12, message = "DB 에러", payload = ParameterDatabaseException.class)
    private String agentType;

    @ParamAlias("noti_type")
    private String notiType;

    private String accessKey;
    private String cpId;
    private String stbMac;
}

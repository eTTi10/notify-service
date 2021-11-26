package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;
import java.util.Arrays;
import java.util.List;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, Groups.C8.class, PushRequestBodySingleVo.class})
public class PushRequestBodySingleVo {

    @NotBlank(message = "app_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)
    @Size(max = 256, message = "파라미터 app_id는 값이 256 이하이어야 함", groups = Groups.C2.class)
    @JsonProperty("app_id")
    private String appId;

    @NotBlank(message = "service_id 파라미터값이 전달이 안됨", groups = Groups.C3.class)
    @JsonProperty("service_id")
    private String serviceId;

    //@NotNull(message = "push_type 파라미터는 값이 G 나 A 이어야 함", groups = Groups.C5.class)
    //@Pattern(regexp = "^[gaGA]]?$", message = "push_type 파라미터는 값이 G 나 A 이어야 함", groups = Groups.C6.class)
    @JsonProperty("push_type")
    private String pushType;

    /** 보낼 메시지 */
    @NotBlank(message = "필수 BODY DATA 미존재[msg]", payload = ParameterExceedMaxSizeException.class, groups = Groups.C7.class)
    @JsonProperty("msg")
    private String msg;

    /** 추가할 항목 입력(name!^value) */
    @NotEmpty(message = "필수 BODY DATA 미존재[items]", payload = ParameterExceedMaxSizeException.class, groups = Groups.C8.class)
    //@Size(max = 1, message = "최대 호출횟수 초과", groups = Groups.C8.class)  // 1120
    @JsonProperty("items")
    private List<String> items;

    /** 사용자 ID */
    @NotNull(message = "reg_id 파라미터값이 전달이 안됨", groups = Groups.C4.class)
    @JsonProperty("reg_id")
    private String regId;

    public String getPushType() {
        return StringUtils.isBlank(pushType) ? "G" : pushType; //Default G
    }

    public PushRequestSingleDto convert() {
        return PushRequestSingleDto.builder()
                .appId(getAppId())
                .serviceId(getServiceId())
                .pushType(getPushType())
                .msg(getMsg())
                .items(getItems())
                .regId(getRegId())
                .build();
    }

}

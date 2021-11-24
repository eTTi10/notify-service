package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, PushRequestParamMultiVo.class})
public class PushRequestParamMultiVo {

    @NotBlank(message = "app_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)
    //@Size(max = 256, message = "파라미터 app_id는 값이 256 이하이어야 함", groups = Groups.C2.class)
    @ParamAlias("app_id")
    private String appId;

    @NotBlank(message = "service_id 파라미터값이 전달이 안됨", groups = Groups.C4.class)
    @ParamAlias("service_id")
    private String serviceId;

    @ParamAlias("multi_count")
    private String multiCount;

    @ParamAlias("push_type")
    private String pushType;

    public String getPushType() {
        return StringUtils.isBlank(pushType) ? "G" : pushType; //Default G
    }

}

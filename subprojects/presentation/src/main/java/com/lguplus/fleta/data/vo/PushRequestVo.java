package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, PushRequestVo.class})
public class PushRequestVo {

    @NotBlank(message = "app_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)
    @Size(max = 256, message = "파라미터 app_id는 값이 256 이하이어야 함", groups = Groups.C2.class)
    @ParamAlias("app_id")
    private String appId;

    @NotBlank(message = "service_id 파라미터값이 전달이 안됨", groups = Groups.C4.class)
    @ParamAlias("service_id")
    private String serviceId;

    @Pattern(regexp = "^[gaGA]]?$", message = "push_type 파라미터는 값이 G 나 A 이어야 함", groups = Groups.C6.class)
    @ParamAlias("push_type")
    private String pushType;

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

}

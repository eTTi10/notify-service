package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.validation.Groups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;
import java.util.List;


@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, Groups.C8.class, PushRequestBodyMultiVo.class})
@ApiModel(value = "Push (Multi) 요청 VO", description = "Push (Multi) 요청 VO")
public class PushRequestBodyMultiVo {

    @NotBlank(message = "app_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)
    @Size(max = 256, message = "파라미터 app_id는 값이 256 이하이어야 함", groups = Groups.C2.class)
    @JsonProperty("app_id")
    @ApiModelProperty(position = 1, example = "lguplushdtvgcm", value = "어플리케이션 ID")
    private String appId;

    @NotBlank(message = "service_id 파라미터값이 전달이 안됨", groups = Groups.C3.class)
    @JsonProperty("service_id")
    @ApiModelProperty(position = 2, example = "30015", value = "서비스 등록시 부여받은 Unique ID")
    private String serviceId;

    @NotNull(message = "push_type 파라미터는 값이 G 나 A 이어야 함", groups = Groups.C4.class)
    @Pattern(regexp = "^[GA]$", message = "push_type 파라미터는 값이 G 나 A 이어야 함", groups = Groups.C4.class)
    @JsonProperty("push_type")
    @ApiModelProperty(position = 3, example = "G", value = "Push 발송 타입 (G: 안드로이드, A: 아이폰)", allowableValues = "G, A")
    private String pushType;

    /** 보낼 메시지 */
    @NotBlank(message = "필수 BODY DATA 미존재[msg]", payload = ParameterExceedMaxSizeException.class, groups = Groups.C7.class)
    @JsonProperty("msg")
    @ApiModelProperty(position = 4, example = "\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"", value = "보낼 메시지")
    private String msg;

    /** 추가할 항목 입력(name!^value) */
    @JsonProperty("items")
    @ApiModelProperty(position = 5, example = "[badge!^1, sound!^ring.caf, cm!^aaaa]", value = "추가할 항목(name!^value)")
    private List<String> items;

    /** 사용자 ID */
    @NotEmpty(message = "필수 BODY DATA 미존재[users]", payload = ParameterExceedMaxSizeException.class, groups = Groups.C8.class)
    @Size(max = 5000, message = "최대 호출횟수 초과", groups = Groups.C8.class)  // 1120
    @ApiModelProperty(position = 6, example = "[MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=]", value = "사용자 ID")
    private List<String> users;

}

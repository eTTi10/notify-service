package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.validation.Groups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;

/**
 * 단건푸시등록 요청 VO
 */
@Getter
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, HttpPushSingleRequestVo.class})
@ApiModel(value = "단건푸시등록 요청 VO", description = "단건푸시등록 요청 VO")
public class HttpPushSingleRequestVo {

    /**
     * 어플리케이션 ID
     */
    @NotBlank(message = "app_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)
    @Size(max = 256, message = "파라미터 app_id는 값이 256 이하이어야 함", groups = Groups.C2.class)
    @JsonProperty("app_id")
    @ApiModelProperty(position = 1, example = "lguplushdtvgcm", value = "어플리케이션 ID")
    private String applicationId;

    /**
     * 서비스 등록시 부여받은 Unique ID
     */
    @NotBlank(message = "service_id 파라미터값이 전달이 안됨", groups = Groups.C3.class)
    @JsonProperty("service_id")
    @ApiModelProperty(position = 2, example = "30015", value = "서비스 등록시 부여받은 Unique ID")
    private String serviceId;

    /**
     * Push 발송 타입 (G: 안드로이드, A: 아이폰)
     */
    @NotNull(message = "push_type 파라미터는 값이 G 나 A 이어야 함", groups = Groups.C4.class)
    @Pattern(regexp = "^[gaGA]$", message = "push_type 파라미터는 값이 G 나 A 이어야 함", groups = Groups.C4.class)
    @JsonProperty("push_type")
    @ApiModelProperty(position = 3, example = "G", value = "Push 발송 타입 (G: 안드로이드, A: 아이폰)", allowableValues = "g, a, G, A")
    private String pushType;

    /**
     * 사용자 ID
     */
    @NotEmpty(message = "필수 BODY DATA 미존재[users]", payload = ParameterExceedMaxSizeException.class, groups = Groups.C6.class)
    @Size(max = 1, message = "최대 호출횟수 초과", groups = Groups.C7.class)  // 1120
    @ApiModelProperty(position = 4, example = "[\"01099991234\"]", value = "사용자 ID")
    private List<String> users;

    /**
     * 보낼 메시지
     */
    @NotBlank(message = "필수 BODY DATA 미존재[msg]", payload = ParameterExceedMaxSizeException.class, groups = Groups.C5.class)
    @JsonProperty("msg")
    @ApiModelProperty(position = 5, example = "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", value = "보낼 메시지")
    private String message;

    /**
     * 추가할 항목 입력(name!^value)
     */
    @ApiModelProperty(position = 6, example = "[\"badge!^1\", \"sound!^ring.caf\", \"cm!^aaaa\"]", value = "추가할 항목(name!^value)")
    private List<String> items;

}

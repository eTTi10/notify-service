package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;

import javax.validation.GroupSequence;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 멀티푸시등록 요청 VO
 *
 */
@Getter
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, Groups.C7.class, HttpPushMultiRequestVo.class})
public class HttpPushMultiRequestVo {

    /** 어플리케이션 ID */
    @NotBlank(message = "app_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)
    @Size(max = 256, message = "파라미터 app_id는 값이 256 이하이어야 함", groups = Groups.C2.class)
    @JsonProperty("app_id")
    private String appId;

    /** 서비스 등록시 부여받은 Unique ID */
    @NotBlank(message = "service_id 파라미터값이 전달이 안됨", groups = Groups.C3.class)
    @JsonProperty("service_id")
    private String serviceId;

    /** Push발송 타입 (G: 안드로이드, A: 아이폰) */
    @NotNull(message = "push_type 파라미터는 값이 G 나 A 이어야 함", groups = Groups.C4.class)
    @Pattern(regexp = "^[gaGA]$", message = "push_type 파라미터는 값이 G 나 A 이어야 함", groups = Groups.C4.class)
    @JsonProperty("push_type")
    private String pushType;

    /** 보낼 메시지 */
    @NotBlank(message = "필수 BODY DATA 미존재[msg]", payload = ParameterExceedMaxSizeException.class, groups = Groups.C5.class)
    private String msg;

    /** 추가할 항목 입력(name!^value) */
    private List<String> items;

    /** 사용자 ID */
    @NotEmpty(message = "필수 BODY DATA 미존재[users]", payload = ParameterExceedMaxSizeException.class, groups = Groups.C6.class)
    @Size(max = 5000, message = "최대 호출횟수 초과", groups = Groups.C7.class)  // 1120
    private List<String> users;

    /** 초당 최대 Push 전송 허용 갯수  */
    @JsonProperty("multi_count")
    private Integer multiCount;

    public HttpPushMultiRequestDto convert() {
        return HttpPushMultiRequestDto.builder()
                .appId(getAppId())
                .serviceId(getServiceId())
                .pushType(getPushType().toUpperCase())
                .msg(getMsg())
                .items(getItems())
                .users(getUsers())
                .multiCount(getMultiCount())
                .build();
    }

}

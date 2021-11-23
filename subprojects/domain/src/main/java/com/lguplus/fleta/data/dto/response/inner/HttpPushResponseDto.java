package com.lguplus.fleta.data.dto.response.inner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.swing.text.MaskFormatter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Http Push 에서 사용하는 공통 응답결과 DTO
 *
 */
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
public class HttpPushResponseDto {

    /** 응답코드 */
    @Builder.Default private String code = "200";

    /** 응답메시지 */
    @Builder.Default private String message = "Success";

    /** 메시지전송 실패 사용자 */
    @JsonProperty("fail_users")
    private List<String> failUsers;

}

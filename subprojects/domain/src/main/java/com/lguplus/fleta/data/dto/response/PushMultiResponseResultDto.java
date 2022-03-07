package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 *
 * @author
 * @since 1.0
 */
@SuperBuilder
public class PushMultiResponseResultDto implements CommonResponseDto {

	private String flag;

	private String message;

	@JsonProperty("fail_users")
	@ApiModelProperty(position = 3, value = "메시지전송 실패 사용자")
	private List<String> failUsers;

	@Override
	public String getFlag() {
		return flag;
	}

	@Override
	public String getMessage() {
		return message;
	}
}

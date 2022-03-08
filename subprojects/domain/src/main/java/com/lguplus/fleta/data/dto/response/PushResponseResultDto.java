package com.lguplus.fleta.data.dto.response;

import lombok.experimental.SuperBuilder;

/**
 *
 * @author
 * @since 1.0
 */
@SuperBuilder
public class PushResponseResultDto implements CommonResponseDto {

	private String flag;

	private String message;

	@Override
	public String getFlag() {
		return flag;
	}

	@Override
	public String getMessage() {
		return message;
	}
}

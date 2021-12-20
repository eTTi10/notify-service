package com.lguplus.fleta.data.dto.response;

import lombok.experimental.SuperBuilder;

/**
 *
 * @author Minwoo Lee
 * @since 1.0
 */
@SuperBuilder
public class SuccessResponseDto implements CommonResponseDto {

	/**
	 *
	 */
	private static final String SUCCESS_FLAG = "0000";

	/**
	 *
	 */
	private static final String SUCCESS_MESSAGE = "성공";

	@Override
	public String getFlag() {
		return SUCCESS_FLAG;
	}

	@Override
	public String getMessage() {
		return SUCCESS_MESSAGE;
	}
}

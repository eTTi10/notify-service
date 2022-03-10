package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.lguplus.fleta.data.dto.PlainTextibleDto;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author Minwoo Lee
 * @since 1.0
 */
@JacksonXmlRootElement(localName = "error")
public interface CommonErrorResponseDto extends PlainTextibleDto {

    @ApiModelProperty(position=1, dataType="string", value="순번: 1<br>자리수: 4<br>설명: 성공여부 코드값", example="0000")
	@JsonGetter("code")
	String getFlag();

    @ApiModelProperty(position=2, dataType="string", value="순번: 2<br>자리수: 50<br>설명: 결과 메시지", example="성공")
	@JsonGetter("message")
	String getMessage();

	@Override
	default String toPlainText() {
		String columnSep = "!^";
		return String.join(columnSep, getFlag(), getMessage());
	}
}

package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.lguplus.fleta.data.dto.PlainTextibleDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author mwlee
 * @since 1.0
 */
@Getter
@SuperBuilder(builderMethodName = "genericRecordsetResponseBuilder")
@JsonPropertyOrder({"flag", "message", "total_count", "recordset"})
public class GenericRecordsetResponseDto<S> extends SuccessResponseDto {

	/**
	 *
	 */
    @ApiModelProperty(position=3, dataType="string", value="순번: 3<br>자리수: 3<br>설명: 검색 갯수<br>검색된 앨범들의 총 갯수", example="1")
	@JsonProperty("total_count")
	private int totalCount;

	/**
	 *
	 */
    @ApiModelProperty(position=1000, dataType="array")
	@JsonProperty("recordset")
	@JacksonXmlElementWrapper(localName="recordset")
	@JacksonXmlProperty(localName="record")
	private List<S> recordset;

	@Override
	public String toPlainText() {

		final StringBuilder buffer = new StringBuilder()
				.append(getTotalCount());
		final List<S> recordset = getRecordset();
		if (recordset != null && !recordset.isEmpty()) {
			buffer.append(Separator.RECORD)
					.append(recordset.stream()
							.filter(record -> record instanceof PlainTextibleDto)
							.map(record -> ((PlainTextibleDto)record).toPlainText())
							.collect(Collectors.joining(Separator.ROW)));
		}

		return String.join(Separator.COLUMN,
				super.toPlainText(), buffer);
	}
}

package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lguplus.fleta.data.type.response.InnerResponseDataType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * HTTP API 표준 응답 Result
 * @version 0.1.1
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class InnerResponseResultDto<T> {

    private T data;
    private InnerResponseDataType dataType;
    private int dataCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private InnerResponseResultPageDto dataPage;

    private InnerResponseResultDto(T data) {
        this.data = data;
        this.dataType = InnerResponseDataType.of(data);
        this.dataCount = InnerResponseDataType.sizeOf(data);
    }

    public static <T> InnerResponseResultDto<T> of(T data) {
        return new InnerResponseResultDto<>(data);
    }

    public void setDataPage(int page, int rowSize, int pageCount) {
        this.dataPage = InnerResponseResultPageDto.of(page, rowSize, pageCount);
    }
}

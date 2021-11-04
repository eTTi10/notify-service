package com.lguplus.fleta.data.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * HTTP API 표준 응답 Result Page
 * @version 0.1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class InnerResponseResultPageDto {

    private int page;
    private int rowSize;
    private int pageCount;

    public static InnerResponseResultPageDto of(int page, int rowSize, int pageCount) {
        return new InnerResponseResultPageDto(page, rowSize, pageCount);
    }
}

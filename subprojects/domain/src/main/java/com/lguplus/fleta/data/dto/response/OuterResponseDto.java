package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

/**
 * HTTP API 표준 응답
 * @version 0.1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class OuterResponseDto {

    private OuterInnerResponseDto error;

    public OuterResponseDto(OuterInnerResponseDto info) {
        this.error = info;
    }

    public static <T> OuterResponseDto of(OuterInnerResponseDto dto) {
        return new OuterResponseDto(dto);
    }

    @Builder
    @Getter
    @ToString
    @JacksonXmlRootElement(localName = "error")
    public static class OuterInnerResponseDto {
        String code;
        String message;

        public String toPlainText() {
            return String.join(CommonResponseDto.Separator.COLUMN,
                    getCode(), getMessage());
        }
    }



}
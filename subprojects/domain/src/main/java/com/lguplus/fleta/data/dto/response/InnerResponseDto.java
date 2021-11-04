package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.data.type.response.InnerResponseDataType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP API 표준 응답
 * @version 0.1.0
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class InnerResponseDto<T> {

    private String code;
    private String message;
    private InnerResponseResultDto<T> result;
    private List<InnerResponseErrorDto> errors;

    @JsonIgnore
    protected HttpStatus httpStatus;

    public InnerResponseDto(InnerResponseCodeType responseCodeType) {
        this.code = responseCodeType.code();
        this.message = responseCodeType.message();
        this.httpStatus = responseCodeType.getHttpStatus();
    }

    public InnerResponseDto(InnerResponseCodeType responseCodeType, T data) {
        this.code = responseCodeType.code();
        this.message = responseCodeType.message();
        this.httpStatus = responseCodeType.getHttpStatus();
        this.result = InnerResponseResultDto.of(data);
    }

    public static <T> InnerResponseDto<T> of(InnerResponseCodeType responseCodeType) {
        return new InnerResponseDto<>(responseCodeType);
    }

    public static <T> InnerResponseDto<T> of(InnerResponseCodeType responseCodeType, T data) {
        return new InnerResponseDto<>(responseCodeType, data);
    }

    public static <T> InnerResponseDto<T> of(T data) {
        int dataSize = InnerResponseDataType.sizeOf(data);
        InnerResponseCodeType responseCodeType = dataSize > 0 ? InnerResponseCodeType.OK : InnerResponseCodeType.NO_CONTENT;
        return new InnerResponseDto<>(responseCodeType, data);
    }

    public static <T> ResponseEntity<InnerResponseDto<T>> toResponseEntity(InnerResponseCodeType responseCodeType) {
        InnerResponseDto<T> responseDto = InnerResponseDto.of(responseCodeType);
        return new ResponseEntity<>(responseDto, responseDto.getHttpStatus());
    }

    public static <T> ResponseEntity<InnerResponseDto<T>> toResponseEntity(InnerResponseCodeType responseCodeType, T data) {
        InnerResponseDto<T> responseDto = InnerResponseDto.of(responseCodeType, data);
        return new ResponseEntity<>(responseDto, responseDto.getHttpStatus());
    }

    public static <T> ResponseEntity<InnerResponseDto<T>> toResponseEntity(T data) {
        InnerResponseDto<T> responseDto = InnerResponseDto.of(data);
        return new ResponseEntity<>(responseDto, responseDto.getHttpStatus());
    }

    public ResponseEntity<InnerResponseDto<T>> toResponseEntity() {
        return new ResponseEntity<>(this, this.getHttpStatus());
    }

    @JsonIgnore
    public int getResponseErrorSize() {
        return this.errors != null ? this.errors.size() : 0;
    }

    public boolean hasResponseError() {
        return getResponseErrorSize() > 0;
    }

    public void addResponseError(InnerResponseErrorDto error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
    }

    public void addResponseErrors(List<InnerResponseErrorDto> errors) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.addAll(errors);
    }
}

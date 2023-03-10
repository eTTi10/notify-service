package com.lguplus.fleta.advice.exhandler;

import com.lguplus.fleta.data.dto.response.ErrorResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseErrorDto;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.data.type.response.InnerResponseErrorType;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.exhandler.ErrorResponseResolver;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * MSA간 통신 Controller Advice
 *
 * @version 0.1.0
 */
@Slf4j
@RestControllerAdvice("com.lguplus.fleta.api.inner")
public class InnerControllerAdvice {

    /**
     *
     */
    private final ErrorResponseResolver errorResponseResolver;

    public InnerControllerAdvice(final ErrorResponseResolver errorResponseResolver) {
        this.errorResponseResolver = errorResponseResolver;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

    /**
     * 파라미터 유효성 Exception Handler
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<InnerResponseDto<Object>> parameterValidateExceptionHandler(BindException e) {
        log.debug("[parameterValidateExceptionHandler] ex", e);
        InnerResponseDto<Object> responseDto = InnerResponseDto.of(InnerResponseCodeType.BAD_REQUEST);

        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                String field = fieldError.getField();
                String defaultMessage = fieldError.getDefaultMessage();
                String detailMessage = "[" + field + "] " + defaultMessage;
                responseDto.addResponseError(InnerResponseErrorDto.of(InnerResponseErrorType.PARAMETER_ERROR, detailMessage));
            }
        }
        return responseDto.toResponseEntity();
    }

    @ExceptionHandler(NotifyRuntimeException.class)
    public ResponseEntity<InnerResponseDto<ErrorResponseDto>> pushRuntimeExceptionHandler(NotifyRuntimeException ex) {
        log.debug("[NotifyRuntimeException] ex:", ex);

        ErrorResponseDto errorResponseDto = errorResponseResolver.resolve(ex);

        InnerResponseDto<ErrorResponseDto> responseDto = InnerResponseDto.of(ex.getInnerResponseCodeType(), errorResponseDto);

        return responseDto.toResponseEntity();
    }

    @ExceptionHandler(HttpPushCustomException.class)
    public ResponseEntity<InnerResponseDto<ErrorResponseDto>> httpPushCustomExceptionHandler(HttpPushCustomException ex) {
        log.debug("[HttpPushCustomException] ex:", ex);

        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().flag(ex.getCode()).message(ex.getMessage()).build();

        InnerResponseDto<ErrorResponseDto> responseDto = InnerResponseDto.of(ex.getInnerResponseCodeType(), errorResponseDto);

        return responseDto.toResponseEntity();
    }

    /**
     * Global Exception Handler
     */
    @ExceptionHandler
    public ResponseEntity<InnerResponseDto<Object>> globalExceptionHandler(Exception e) {
        log.error("[globalExceptionHandler] ex", e);
        return InnerResponseDto.toResponseEntity(InnerResponseCodeType.INTERNAL_SERVER_ERROR);
    }
}

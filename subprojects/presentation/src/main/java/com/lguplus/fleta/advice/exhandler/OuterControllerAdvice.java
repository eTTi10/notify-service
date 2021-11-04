package com.lguplus.fleta.advice.exhandler;

import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.exhandler.ErrorResponseResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice("com.lguplus.fleta.api.outer")
public class OuterControllerAdvice {

    /**
     *
     */
    private final ErrorResponseResolver errorResponseResolver;

    /**
     *
     * @param errorResponseResolver
     */
    public OuterControllerAdvice(final ErrorResponseResolver errorResponseResolver) {

        this.errorResponseResolver = errorResponseResolver;
    }

    /**
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<CommonResponseDto> handleBindException(final HttpServletRequest request,
                                                                 final BindException ex) {
        log.info(ex.getMessage(), ex);

        return ResponseEntity.ok().body(errorResponseResolver.resolve(ex));
    }

    /**
     *
     * @param th
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<CommonResponseDto> handleThrowable(final HttpServletRequest request,
                                                             final Throwable th) {
        log.error(th.getMessage(), th);

        return ResponseEntity.ok().body(errorResponseResolver.resolve(th));
    }
}

package com.lguplus.fleta.advice.exhandler;

import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.ErrorResponseDto;
import com.lguplus.fleta.data.vo.error.ErrorResponseVo;
import com.lguplus.fleta.exhandler.CustomErrorResponseConverter;
import com.lguplus.fleta.exhandler.ErrorResponseResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice("com.lguplus.fleta.api.outer")
public class OuterControllerAdvice {

    /**
     *
     */
    private static final Map<String, CustomErrorResponseConverter> CUSTOM_ERROR_RESPONSE_CONVERTERS = new HashMap<>();

    static {
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /mims/sendSms",
                new CustomErrorResponseConverter(ErrorResponseVo.class, "errorResponseBuilder"));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /mims/sendPushCode",
                new CustomErrorResponseConverter(ErrorResponseVo.class, "errorResponseBuilder"));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("GET /smartux/UXSimpleJoin",
                new CustomErrorResponseConverter(ErrorResponseVo.class, "errorResponseBuilder"));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("GET /smartux/comm/latest",
                new CustomErrorResponseConverter(ErrorResponseVo.class, "errorResponseBuilder"));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /smartux/comm/latest",
                new CustomErrorResponseConverter(ErrorResponseVo.class, "errorResponseBuilder"));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("DELETE /smartux/comm/latest",
                new CustomErrorResponseConverter(ErrorResponseVo.class, "errorResponseBuilder"));
    }

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

    @InitBinder
    public void initBinder(final WebDataBinder binder) {

        binder.initDirectFieldAccess();
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
        return ResponseEntity.ok().body(getCustomErrorResponse(request, errorResponseResolver.resolve(ex)));
    }

    /**
     *  /mims/sendPushCode RequestBody가 Null일 때 Exception 처리 용
     * @param request
     * @param th
     * @return
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponseDto> httpException(final HttpServletRequest request,
                                                final Throwable th) {
        return ResponseEntity.ok().body(getCustomErrorResponse(request, ErrorResponseDto.builder().flag("9999").message("기타 에러").build()));
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
        return ResponseEntity.ok().body(getCustomErrorResponse(request, errorResponseResolver.resolve(th)));
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    private CommonResponseDto getCustomErrorResponse(final HttpServletRequest request,
                                                          final ErrorResponseDto response) {
        final String uri = request.getMethod() + " " + request.getRequestURI();
        final CustomErrorResponseConverter converter = CUSTOM_ERROR_RESPONSE_CONVERTERS.get(uri);
        if (converter == null) {
            return response;
        }
        try {
            return converter.convert(response);
        } catch (final Throwable e) {
            log.warn(e.getMessage(), e);
            return response;
        }
    }
}
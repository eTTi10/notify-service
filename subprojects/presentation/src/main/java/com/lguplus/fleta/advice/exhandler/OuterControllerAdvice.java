package com.lguplus.fleta.advice.exhandler;

import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.ErrorResponseDto;
import com.lguplus.fleta.data.vo.error.ErrorResponseVo;
import com.lguplus.fleta.exception.NotifyRuntimeException;
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

    private static final Map<String, CustomErrorResponseConverter> CUSTOM_ERROR_RESPONSE_CONVERTERS = new HashMap<>();

    private static final String defaultConverterNm = "**DEFAULT**";

    static {
        //Default
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put(defaultConverterNm,
                new CustomErrorResponseConverter(ErrorResponseVo.class, "errorResponseBuilder"));

        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /mims/sendPushCode",
                new CustomErrorResponseConverter(ErrorResponseVo.class, "errorResponseBuilder"));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /mims/sendSms",
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
    public ResponseEntity<Object> handleBindException(final HttpServletRequest request,
                                                                 final BindException ex) {
        log.info(ex.getMessage(), ex);

        return ResponseEntity.ok().body(getCustomErrorResponse(request, errorResponseResolver.resolve(ex)));
    }


    @ExceptionHandler(NotifyRuntimeException.class)
    public ResponseEntity<Object> handleNotifyException(final HttpServletRequest request,
                                                        final Throwable th) {
        log.error(th.getMessage(), th);
        return ResponseEntity.ok().body(getCustomErrorResponse(request, errorResponseResolver.resolve(th)));
    }


    /**
     *
     * @param th
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleThrowable(final HttpServletRequest request,
                                                             final Throwable th) {
        log.error(th.getMessage(), th);

        return ResponseEntity.ok().body(getCustomErrorResponse(request, errorResponseResolver.resolve(th)));
    }


    private Object getCustomErrorResponse(final HttpServletRequest request, final ErrorResponseDto response) {

        final String uri = request.getMethod() + " " + request.getRequestURI();
        final CustomErrorResponseConverter converter = CUSTOM_ERROR_RESPONSE_CONVERTERS.get(uri);

        try {
            if (converter == null) {
                final CustomErrorResponseConverter converterDefault = CUSTOM_ERROR_RESPONSE_CONVERTERS.get(defaultConverterNm);
                return converterDefault.convert(response);
            }
            return converter.convert(response);
        } catch (final Throwable e) {
            log.warn(e.getMessage(), e);
            return response;
        }
    }


}


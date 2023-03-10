package com.lguplus.fleta.advice.exhandler;

import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.ErrorResponseDto;
import com.lguplus.fleta.data.vo.error.ErrorResponseVo;
import com.lguplus.fleta.data.vo.error.TrailedErrorResponseVo;
import com.lguplus.fleta.exception.UndefinedException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice("com.lguplus.fleta.api.outer")
public class OuterControllerAdvice {

    private static final Map<String, CustomErrorResponseConverter> CUSTOM_ERROR_RESPONSE_CONVERTERS = new HashMap<>();
    private static final Map<String, List<String>> UNCONVERTIBLE_ERROR_CODE_PATTERNS = new HashMap<>();

    static {
        final String builderName = "errorResponseBuilder";

        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /mims/sendSms",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /mims/sendPushCode",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("GET /smartux/UXSimpleJoin.php",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("GET /smartux/comm/latest",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /smartux/comm/latest",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("DELETE /smartux/comm/latest",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /mobile/hdtv/v1/push/deviceinfo",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("DELETE /mobile/hdtv/v1/push/deviceinfo",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("PUT /mobile/hdtv/v1/push/deviceinfo",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("GET/videolte/musicshow/push",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST/videolte/musicshow/push",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("DELETE/videolte/musicshow/push",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /mobile/hdtv/v1/latest",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("POST /mobile/hdtv/comm/latest",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("DELETE /mobile/hdtv/v1/latest",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));
        CUSTOM_ERROR_RESPONSE_CONVERTERS.put("DELETE /mobile/hdtv/comm/latest",
            new CustomErrorResponseConverter(ErrorResponseVo.class, builderName));

        UNCONVERTIBLE_ERROR_CODE_PATTERNS.put("POST /mims/sendPushCode", List.of("^[^5].*$"));
    }

    /**
     *
     */
    private final ErrorResponseResolver errorResponseResolver;

    /**
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
     * @param /mobile/mims/deviceinfo db?????? exception ?????? ?????? result??? ?????? response ???????????? ????????? ????????????
     * @return
     */
    @ExceptionHandler(UndefinedException.class)
    public ResponseEntity<CommonResponseDto> handleBindException(final HttpServletRequest request,
        final UndefinedException ex) {
        log.info(ex.getMessage(), ex);
        return ResponseEntity.ok().body(ErrorResponseDto.builder().flag("9999").message("?????? ??????").build());
    }

    /**
     * /mims/sendPushCode RequestBody??? Null??? ??? Exception ?????? ???
     *
     * @param request
     * @param th
     * @return
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponseDto> httpException(final HttpServletRequest request,
        final Throwable th) {
        log.error(th.getMessage(), th);
        return ResponseEntity.ok().body(getCustomErrorResponse(request, ErrorResponseDto.builder().flag("9999").message("?????? ??????").build()));
    }

    /**
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
     * @param request
     * @param response
     * @return
     */
    private CommonResponseDto getCustomErrorResponse(final HttpServletRequest request,
        final ErrorResponseDto response) {
        final String uri = request.getMethod() + " " + request.getRequestURI();

        if (Optional.ofNullable(UNCONVERTIBLE_ERROR_CODE_PATTERNS.get(uri)).orElse(List.of()).stream()
            .anyMatch(regexp -> response.getFlag().matches(regexp))) {
            return response;
        }

        final CustomErrorResponseConverter converter = response.getFlag().endsWith("_") ?
                new CustomErrorResponseConverter(TrailedErrorResponseVo.class, "trailedErrorResponseBuilder") :
                CUSTOM_ERROR_RESPONSE_CONVERTERS.get(response.getFlag());
        if (converter == null) {
            return response;
        }
        try {
            return converter.convert(response);
        } catch (final Exception e) {
            log.warn(e.getMessage(), e);
            return response;
        }
    }
}
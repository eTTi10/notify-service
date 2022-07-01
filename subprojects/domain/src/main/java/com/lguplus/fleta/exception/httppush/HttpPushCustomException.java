package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import lombok.Getter;

/**
 * @author Taekuk Song
 * @since 1.0
 */
@Getter
public class HttpPushCustomException extends NotifyRuntimeException {

    private final Integer statusCode;
    private final String code;

    /**
     *
     */
    public HttpPushCustomException(final Integer statusCode, final String code) {

        super();
        this.statusCode = statusCode;
        this.code = code;
    }

    /**
     * @param message
     */
    public HttpPushCustomException(final Integer statusCode, final String code, final String message) {

        super(message);
        this.statusCode = statusCode;
        this.code = code;
    }

    /**
     * @param message
     * @param cause
     */
    public HttpPushCustomException(final Integer statusCode, final String code, final String message, final Throwable cause) {

        super(message, cause);
        this.statusCode = statusCode;
        this.code = code;
    }

    /**
     * @param cause
     */
    public HttpPushCustomException(final Integer statusCode, final String code, final Throwable cause) {

        super(cause);
        this.statusCode = statusCode;
        this.code = code;
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }
}

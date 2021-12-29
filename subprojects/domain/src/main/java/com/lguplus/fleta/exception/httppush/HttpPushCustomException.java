package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;
import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @author Taekuk Song
 * @since 1.0
 */
@Getter
@Setter
public class HttpPushCustomException extends NotifyHttpPushRuntimeException {

    private Integer statusCode;

    private String code;

    private String message;

    /**
     *
     */
    public HttpPushCustomException() {

        super();
    }

    /**
     *
     * @param message
     */
    public HttpPushCustomException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public HttpPushCustomException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public HttpPushCustomException(final Throwable cause) {

        super(cause);
    }
}

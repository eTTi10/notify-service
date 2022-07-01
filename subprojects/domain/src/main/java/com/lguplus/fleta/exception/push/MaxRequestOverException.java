package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1120
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class MaxRequestOverException extends NotifyRuntimeException {

    /**
     *
     */
    public MaxRequestOverException() {

        super();
    }

    /**
     * @param message
     */
    public MaxRequestOverException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public MaxRequestOverException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public MaxRequestOverException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.PUSH_SERVER_ERROR;
    }
}

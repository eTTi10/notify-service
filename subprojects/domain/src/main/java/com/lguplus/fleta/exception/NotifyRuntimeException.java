package com.lguplus.fleta.exception;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;

/**
 * Exception for error flag 1112
 *
 * @author Taekuk Song
 * @since 1.0
 */
public abstract class NotifyRuntimeException extends RuntimeException {

    public abstract InnerResponseCodeType getInnerResponseCodeType();

    /**
     *
     */
    protected NotifyRuntimeException() {

        super();
    }

    /**
     * @param message
     */
    protected NotifyRuntimeException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    protected NotifyRuntimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    protected NotifyRuntimeException(final Throwable cause) {

        super(cause);
    }
}

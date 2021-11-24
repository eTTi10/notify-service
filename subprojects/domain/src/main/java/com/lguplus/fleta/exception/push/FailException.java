package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1111
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class FailException extends NotifyRuntimeException {

    /**
     *
     */
    public FailException() {

        super();
    }

    /**
     *
     * @param message
     */
    public FailException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public FailException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public FailException(final Throwable cause) {

        super(cause);
    }
}

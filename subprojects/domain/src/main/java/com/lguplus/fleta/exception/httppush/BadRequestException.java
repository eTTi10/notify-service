package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;

/**
 * Exception for error flag 1104
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class BadRequestException extends NotifyHttpPushRuntimeException {

    /**
     *
     */
    public BadRequestException() {

        super();
    }

    /**
     *
     * @param message
     */
    public BadRequestException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public BadRequestException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public BadRequestException(final Throwable cause) {

        super(cause);
    }
}

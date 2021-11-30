package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1104
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class BadRequestException extends NotifyPushRuntimeException {

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

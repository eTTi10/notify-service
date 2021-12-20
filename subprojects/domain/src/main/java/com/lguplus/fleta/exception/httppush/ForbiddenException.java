package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;

/**
 * Exception for error flag 1106
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class ForbiddenException extends NotifyHttpPushRuntimeException {

    /**
     *
     */
    public ForbiddenException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ForbiddenException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ForbiddenException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ForbiddenException(final Throwable cause) {

        super(cause);
    }
}

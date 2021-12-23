package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;

/**
 * Exception for error flag 1107
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class NotFoundException extends NotifyHttpPushRuntimeException {

    /**
     *
     */
    public NotFoundException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotFoundException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotFoundException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotFoundException(final Throwable cause) {

        super(cause);
    }
}
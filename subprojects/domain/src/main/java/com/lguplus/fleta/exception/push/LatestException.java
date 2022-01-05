package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;

/**
 * Exception for error flag 9999
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class LatestException extends NotifyPushRuntimeException {

    /**
     *
     */
    public LatestException() {

        super();
    }

    /**
     *
     * @param message
     */
    public LatestException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public LatestException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public LatestException(final Throwable cause) {

        super(cause);
    }
}

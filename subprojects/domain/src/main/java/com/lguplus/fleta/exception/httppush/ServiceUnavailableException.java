package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;

/**
 * Exception for error flag 1110
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class ServiceUnavailableException extends NotifyHttpPushRuntimeException {

    /**
     *
     */
    public ServiceUnavailableException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ServiceUnavailableException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ServiceUnavailableException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ServiceUnavailableException(final Throwable cause) {

        super(cause);
    }
}

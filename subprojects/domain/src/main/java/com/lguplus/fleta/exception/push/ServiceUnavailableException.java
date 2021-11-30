package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1110
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class ServiceUnavailableException extends NotifyPushRuntimeException {

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

package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1101
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class SocketNotFoundException extends NotifyRuntimeException {

    /**
     *
     */
    public SocketNotFoundException() {

        super();
    }

    /**
     *
     * @param message
     */
    public SocketNotFoundException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SocketNotFoundException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SocketNotFoundException(final Throwable cause) {

        super(cause);
    }
}

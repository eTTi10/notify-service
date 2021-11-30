package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1102
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class SocketException extends NotifyPushRuntimeException {

    /**
     *
     */
    public SocketException() {

        super();
    }

    /**
     *
     * @param message
     */
    public SocketException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SocketException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SocketException(final Throwable cause) {

        super(cause);
    }
}

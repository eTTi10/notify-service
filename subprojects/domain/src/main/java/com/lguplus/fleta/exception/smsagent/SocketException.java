package com.lguplus.fleta.exception.smsagent;

import com.lguplus.fleta.exception.NotifySmsRuntimeException;

/**
 * Exception for error flag 1102
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class SocketException extends NotifySmsRuntimeException {

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

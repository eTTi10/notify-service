package com.lguplus.fleta.exception.smsagent;


import com.lguplus.fleta.exception.NotifySmsRuntimeException;

/**
 * Exception for error flag 1103
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class SocketTimeOutException extends NotifySmsRuntimeException {

    /**
     *
     */
    public SocketTimeOutException() {

        super();
    }

    /**
     *
     * @param message
     */
    public SocketTimeOutException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SocketTimeOutException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SocketTimeOutException(final Throwable cause) {

        super(cause);
    }
}

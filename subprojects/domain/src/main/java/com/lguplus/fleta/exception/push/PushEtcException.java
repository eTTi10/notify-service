package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;

/**
 * Exception for error flag 1108
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class PushEtcException extends NotifyPushRuntimeException {

    /**
     *
     */
    public PushEtcException() {

        super();
    }

    /**
     *
     * @param message
     */
    public PushEtcException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public PushEtcException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public PushEtcException(final Throwable cause) {

        super(cause);
    }
}

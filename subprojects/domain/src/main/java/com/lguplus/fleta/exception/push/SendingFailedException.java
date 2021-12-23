package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1130
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class SendingFailedException extends NotifyPushRuntimeException {

    /**
     *
     */
    public SendingFailedException() {

        super();
    }

    /**
     *
     * @param message
     */
    public SendingFailedException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public SendingFailedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public SendingFailedException(final Throwable cause) {

        super(cause);
    }
}
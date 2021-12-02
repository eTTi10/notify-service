package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;

/**
 * Exception for error flag 1112
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class AcceptedException extends NotifyHttpPushRuntimeException {

    /**
     *
     */
    public AcceptedException() {

        super();
    }

    /**
     *
     * @param message
     */
    public AcceptedException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public AcceptedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public AcceptedException(final Throwable cause) {

        super(cause);
    }
}

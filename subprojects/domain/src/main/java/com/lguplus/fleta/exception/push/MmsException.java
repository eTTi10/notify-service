package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;

/**
 * Exception for error flag 9999
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class MmsException extends NotifyPushRuntimeException {

    /**
     *
     */
    public MmsException() {

        super();
    }

    /**
     *
     * @param message
     */
    public MmsException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public MmsException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public MmsException(final Throwable cause) {

        super(cause);
    }
}

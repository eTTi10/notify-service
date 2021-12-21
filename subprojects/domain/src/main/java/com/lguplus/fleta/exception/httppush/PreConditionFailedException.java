package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;

/**
 * Exception for error flag 1108
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class PreConditionFailedException extends NotifyHttpPushRuntimeException {

    /**
     *
     */
    public PreConditionFailedException() {

        super();
    }

    /**
     *
     * @param message
     */
    public PreConditionFailedException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public PreConditionFailedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public PreConditionFailedException(final Throwable cause) {

        super(cause);
    }
}

package com.lguplus.fleta.exception.push;

import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1108
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class PreConditionFailedException extends NotifyPushRuntimeException {

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

package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;

/**
 * Exception for error flag 9998.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExclusionNumberException extends NotifyHttpPushRuntimeException {

    /**
     *
     */
    public ExclusionNumberException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ExclusionNumberException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ExclusionNumberException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ExclusionNumberException(final Throwable cause) {

        super(cause);
    }
}

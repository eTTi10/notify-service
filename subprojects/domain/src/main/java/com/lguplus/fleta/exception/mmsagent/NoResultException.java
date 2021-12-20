package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.exception.NotifyMmsRuntimeException;

/**
 * Exception for error flag 0001.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class NoResultException extends NotifyMmsRuntimeException {

    /**
     *
     */
    public NoResultException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NoResultException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NoResultException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NoResultException(final Throwable cause) {

        super(cause);
    }
}

package com.lguplus.fleta.exception.mmsagent;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 0001.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class NoResultException extends NotifyRuntimeException {

    @Override
    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.MMS_SERVER_ERROR;
    }

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

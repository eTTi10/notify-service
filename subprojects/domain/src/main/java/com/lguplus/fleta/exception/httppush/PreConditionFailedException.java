package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1108
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class PreConditionFailedException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }

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

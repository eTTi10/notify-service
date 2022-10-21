package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 9998.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExclusionNumberException extends NotifyRuntimeException {

    /**
     *
     */
    public ExclusionNumberException() {

        super();
    }

    /**
     * @param message
     */
    public ExclusionNumberException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ExclusionNumberException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ExclusionNumberException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }
}

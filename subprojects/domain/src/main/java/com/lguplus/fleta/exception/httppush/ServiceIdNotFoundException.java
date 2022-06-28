package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 1115.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ServiceIdNotFoundException extends NotifyRuntimeException {

    /**
     *
     */
    public ServiceIdNotFoundException() {

        super();
    }

    /**
     * @param message
     */
    public ServiceIdNotFoundException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ServiceIdNotFoundException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ServiceIdNotFoundException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }
}

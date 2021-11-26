package com.lguplus.fleta.exception;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;

/**
 * Exception for error flag 1112
 *
 * @author Taekuk Song
 * @since 1.0
 */
public class NotifyOpenApiRuntimeException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.OPENAPI_PUSH_SERVER_ERROR;
    }

    /**
     *
     */
    public NotifyOpenApiRuntimeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotifyOpenApiRuntimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotifyOpenApiRuntimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotifyOpenApiRuntimeException(final Throwable cause) {

        super(cause);
    }
}

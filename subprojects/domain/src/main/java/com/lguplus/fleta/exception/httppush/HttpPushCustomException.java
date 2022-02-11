package com.lguplus.fleta.exception.httppush;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @author Taekuk Song
 * @since 1.0
 */
@Getter
@Setter
public class HttpPushCustomException extends NotifyRuntimeException {

    public InnerResponseCodeType getInnerResponseCodeType()
    {
        return InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR;
    }

    private Integer statusCode;

    private String code;

    private String message;

    /**
     *
     */
    public HttpPushCustomException() {

        super();
    }

    /**
     *
     * @param message
     */
    public HttpPushCustomException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public HttpPushCustomException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public HttpPushCustomException(final Throwable cause) {

        super(cause);
    }
}

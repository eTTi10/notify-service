package com.lguplus.fleta.exception.database;

import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.exception.NotifyRuntimeException;

/**
 * Exception for error flag 8002
 *
 * @author MinSeok Song
 * @since 1.0
 */
public class DataNotExistsInnerException extends NotifyRuntimeException {

    /**
     *
     */
    public DataNotExistsInnerException() {

        super();
    }

    /**
     * @param message
     */
    public DataNotExistsInnerException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public DataNotExistsInnerException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public DataNotExistsInnerException(final Throwable cause) {

        super(cause);
    }

    public InnerResponseCodeType getInnerResponseCodeType() {
        return InnerResponseCodeType.NO_CONTENT;
    }
}

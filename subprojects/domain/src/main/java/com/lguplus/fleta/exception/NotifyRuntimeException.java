package com.lguplus.fleta.exception;

import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;

/**
 * Exception for error flag 1112
 *
 * @author Taekuk Song
 * @since 1.0
 */
public abstract class NotifyRuntimeException extends RuntimeException {

    public abstract InnerResponseCodeType getInnerResponseCodeType();

    /**
     *
     */
    public NotifyRuntimeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NotifyRuntimeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NotifyRuntimeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NotifyRuntimeException(final Throwable cause) {

        super(cause);
    }
}

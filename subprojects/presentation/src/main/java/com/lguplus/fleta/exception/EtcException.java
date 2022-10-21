package com.lguplus.fleta.exception;


import javax.validation.Payload;

/**
 * Exception for error flag 9999.
 *
 * @author UkHwi Kim
 * @since 1.0
 */
public class EtcException extends RuntimeException implements Payload {

    /**
     *
     */
    public EtcException() {

        super();
    }

    /**
     *
     * @param message
     */
    public EtcException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public EtcException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public EtcException(final Throwable cause) {
        super(cause);
    }
}

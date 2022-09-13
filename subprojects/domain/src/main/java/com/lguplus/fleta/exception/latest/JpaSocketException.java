package com.lguplus.fleta.exception.latest;

/**
 * Exception for error flag 1401. 5101 소켓에러
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class JpaSocketException extends RuntimeException {

    /**
     *
     */
    public JpaSocketException() {

        super();
    }

    /**
     * @param message
     */
    public JpaSocketException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public JpaSocketException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public JpaSocketException(final Throwable cause) {

        super(cause);
    }
}

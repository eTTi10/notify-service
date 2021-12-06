package com.lguplus.fleta.exception.latest;

/**
 * Exception for error flag 1401.
 * 5102 소켓 타임 아웃 에러
 * @author Minwoo Lee
 * @since 1.0
 */
public class JpaSocketTimeoutException extends RuntimeException {

    /**
     *
     */
    public JpaSocketTimeoutException() {

        super();
    }

    /**
     *
     * @param message
     */
    public JpaSocketTimeoutException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public JpaSocketTimeoutException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public JpaSocketTimeoutException(final Throwable cause) {

        super(cause);
    }
}

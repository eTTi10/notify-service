package com.lguplus.fleta.exception.coupon;

/**
 * Exception for error flag 1406.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExpiredCouponException extends RuntimeException {

    /**
     *
     */
    public ExpiredCouponException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ExpiredCouponException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ExpiredCouponException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ExpiredCouponException(final Throwable cause) {

        super(cause);
    }
}

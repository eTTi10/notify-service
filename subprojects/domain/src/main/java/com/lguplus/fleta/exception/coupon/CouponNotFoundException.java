package com.lguplus.fleta.exception.coupon;

/**
 * Exception for error flag 1410.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class CouponNotFoundException extends RuntimeException {

    /**
     *
     */
    public CouponNotFoundException() {

        super();
    }

    /**
     *
     * @param message
     */
    public CouponNotFoundException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public CouponNotFoundException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public CouponNotFoundException(final Throwable cause) {

        super(cause);
    }
}

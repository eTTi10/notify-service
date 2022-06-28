package com.lguplus.fleta.exception.coupon;

/**
 * Exception for error flag 1405.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class CouponAlreadyUsedException extends RuntimeException {

    /**
     *
     */
    public CouponAlreadyUsedException() {

        super();
    }

    /**
     * @param message
     */
    public CouponAlreadyUsedException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public CouponAlreadyUsedException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public CouponAlreadyUsedException(final Throwable cause) {

        super(cause);
    }
}

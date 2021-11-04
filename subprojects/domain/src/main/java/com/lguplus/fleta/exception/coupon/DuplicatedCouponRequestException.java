package com.lguplus.fleta.exception.coupon;

/**
 * Exception for error flag 1401.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class DuplicatedCouponRequestException extends RuntimeException {

    /**
     *
     */
    public DuplicatedCouponRequestException() {

        super();
    }

    /**
     *
     * @param message
     */
    public DuplicatedCouponRequestException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public DuplicatedCouponRequestException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public DuplicatedCouponRequestException(final Throwable cause) {

        super(cause);
    }
}

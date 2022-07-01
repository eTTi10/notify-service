package com.lguplus.fleta.exception.coupon;

/**
 * Exception for error flag 1407.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class InvalidCouponOwnerException extends RuntimeException {

    /**
     *
     */
    public InvalidCouponOwnerException() {

        super();
    }

    /**
     * @param message
     */
    public InvalidCouponOwnerException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidCouponOwnerException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public InvalidCouponOwnerException(final Throwable cause) {

        super(cause);
    }
}

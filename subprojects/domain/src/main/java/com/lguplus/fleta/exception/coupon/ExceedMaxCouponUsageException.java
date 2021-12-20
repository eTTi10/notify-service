package com.lguplus.fleta.exception.coupon;

/**
 * Exception for error flag 1409.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExceedMaxCouponUsageException extends RuntimeException {

    /**
     *
     */
    public ExceedMaxCouponUsageException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ExceedMaxCouponUsageException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ExceedMaxCouponUsageException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ExceedMaxCouponUsageException(final Throwable cause) {

        super(cause);
    }
}

package com.lguplus.fleta.exception.coupon;

/**
 * Exception for error flag 1400.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExceedMaxCouponRequestException extends RuntimeException {

    /**
     *
     */
    public ExceedMaxCouponRequestException() {

        super();
    }

    /**
     * @param message
     */
    public ExceedMaxCouponRequestException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ExceedMaxCouponRequestException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ExceedMaxCouponRequestException(final Throwable cause) {

        super(cause);
    }
}

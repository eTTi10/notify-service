package com.lguplus.fleta.exception.coupon;

/**
 * Exception for error flag 1408.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class FailToUseCouponException extends RuntimeException {

    /**
     *
     */
    public FailToUseCouponException() {

        super();
    }

    /**
     * @param message
     */
    public FailToUseCouponException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public FailToUseCouponException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public FailToUseCouponException(final Throwable cause) {

        super(cause);
    }
}

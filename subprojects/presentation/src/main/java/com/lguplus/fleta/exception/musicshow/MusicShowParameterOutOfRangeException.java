package com.lguplus.fleta.exception.musicshow;

import javax.validation.Payload;

/**
 * Exception for error flag 5010.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class MusicShowParameterOutOfRangeException extends RuntimeException implements Payload {

    /**
     *
     */
    public MusicShowParameterOutOfRangeException() {

        super();
    }

    /**
     *
     * @param message
     */
    public MusicShowParameterOutOfRangeException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public MusicShowParameterOutOfRangeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public MusicShowParameterOutOfRangeException(final Throwable cause) {

        super(cause);
    }
}

package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2004.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExceedMaxPlaylistSizeException extends RuntimeException {

    /**
     *
     */
    public ExceedMaxPlaylistSizeException() {

        super();
    }

    /**
     * @param message
     */
    public ExceedMaxPlaylistSizeException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ExceedMaxPlaylistSizeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ExceedMaxPlaylistSizeException(final Throwable cause) {

        super(cause);
    }
}

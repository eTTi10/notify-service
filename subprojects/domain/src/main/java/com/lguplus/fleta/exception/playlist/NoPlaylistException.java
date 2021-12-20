package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2003.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class NoPlaylistException extends RuntimeException {

    /**
     *
     */
    public NoPlaylistException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NoPlaylistException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NoPlaylistException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NoPlaylistException(final Throwable cause) {

        super(cause);
    }
}

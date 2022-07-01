package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2009.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class PlaylistNotExistsException extends RuntimeException {

    /**
     *
     */
    public PlaylistNotExistsException() {

        super();
    }

    /**
     * @param message
     */
    public PlaylistNotExistsException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public PlaylistNotExistsException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public PlaylistNotExistsException(final Throwable cause) {

        super(cause);
    }
}

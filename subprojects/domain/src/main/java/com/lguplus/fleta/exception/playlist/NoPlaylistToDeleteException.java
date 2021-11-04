package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2001.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class NoPlaylistToDeleteException extends RuntimeException {

    /**
     *
     */
    public NoPlaylistToDeleteException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NoPlaylistToDeleteException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NoPlaylistToDeleteException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NoPlaylistToDeleteException(final Throwable cause) {

        super(cause);
    }
}

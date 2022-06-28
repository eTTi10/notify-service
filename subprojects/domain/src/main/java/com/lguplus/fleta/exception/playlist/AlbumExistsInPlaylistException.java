package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2005.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class AlbumExistsInPlaylistException extends RuntimeException {

    /**
     *
     */
    public AlbumExistsInPlaylistException() {

        super();
    }

    /**
     * @param message
     */
    public AlbumExistsInPlaylistException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public AlbumExistsInPlaylistException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public AlbumExistsInPlaylistException(final Throwable cause) {

        super(cause);
    }
}

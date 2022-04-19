package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2010.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class LastPlaylistFolderNotDeletableException extends RuntimeException {

    /**
     *
     */
    public LastPlaylistFolderNotDeletableException() {

        super();
    }

    /**
     *
     * @param message
     */
    public LastPlaylistFolderNotDeletableException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public LastPlaylistFolderNotDeletableException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public LastPlaylistFolderNotDeletableException(final Throwable cause) {

        super(cause);
    }
}

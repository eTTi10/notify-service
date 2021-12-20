package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2006.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class NoPlaylistFolderException extends RuntimeException {

    /**
     *
     */
    public NoPlaylistFolderException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NoPlaylistFolderException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NoPlaylistFolderException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NoPlaylistFolderException(final Throwable cause) {

        super(cause);
    }
}

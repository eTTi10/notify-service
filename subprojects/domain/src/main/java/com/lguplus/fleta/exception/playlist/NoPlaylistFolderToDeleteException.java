package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2008.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class NoPlaylistFolderToDeleteException extends RuntimeException {

    /**
     *
     */
    public NoPlaylistFolderToDeleteException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NoPlaylistFolderToDeleteException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NoPlaylistFolderToDeleteException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NoPlaylistFolderToDeleteException(final Throwable cause) {

        super(cause);
    }
}

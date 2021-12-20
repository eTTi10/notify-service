package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2002.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class NoPlaylistFolderToModifyException extends RuntimeException {

    /**
     *
     */
    public NoPlaylistFolderToModifyException() {

        super();
    }

    /**
     *
     * @param message
     */
    public NoPlaylistFolderToModifyException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NoPlaylistFolderToModifyException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NoPlaylistFolderToModifyException(final Throwable cause) {

        super(cause);
    }
}

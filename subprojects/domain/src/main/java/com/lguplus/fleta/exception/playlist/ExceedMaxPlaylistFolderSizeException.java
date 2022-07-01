package com.lguplus.fleta.exception.playlist;

/**
 * Exception for error flag 2000.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ExceedMaxPlaylistFolderSizeException extends RuntimeException {

    /**
     *
     */
    public ExceedMaxPlaylistFolderSizeException() {

        super();
    }

    /**
     * @param message
     */
    public ExceedMaxPlaylistFolderSizeException(final String message) {

        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ExceedMaxPlaylistFolderSizeException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     * @param cause
     */
    public ExceedMaxPlaylistFolderSizeException(final Throwable cause) {

        super(cause);
    }
}

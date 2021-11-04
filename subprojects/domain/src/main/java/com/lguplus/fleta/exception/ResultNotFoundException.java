package com.lguplus.fleta.exception;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Optional;

/**
 * Exception for error flag 5010.
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class ResultNotFoundException extends RuntimeException {

    /**
     *
     */
    public ResultNotFoundException() {

        super();
    }

    /**
     *
     * @param message
     */
    public ResultNotFoundException(final String message) {

        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ResultNotFoundException(final String message, final Throwable cause) {

        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ResultNotFoundException(final Throwable cause) {

        super(cause);
    }

    public ResultNotFoundException(List<ObjectError> allErrors, String errorMsg) {
        super(errorMsg);
        Optional.ofNullable(allErrors).ifPresent(errors -> {

//            log.error(errors.stream()
//                    .map(e -> String.format("Error in object '%s': %s", e.getObjectName(), e.getDefaultMessage()))
//                    .collect(Collectors.joining(" && ")));
        });
    }

}

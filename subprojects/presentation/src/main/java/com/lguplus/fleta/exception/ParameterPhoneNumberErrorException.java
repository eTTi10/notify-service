package com.lguplus.fleta.exception;

import javax.validation.Payload;

    /**
     * Exception for error flag 1502.
     *
     * @author Minwoo Lee
     * @since 1.0
     */
    public class ParameterPhoneNumberErrorException extends RuntimeException implements Payload {

        /**
         *
         */
        public ParameterPhoneNumberErrorException() {

            super();
        }

        /**
         *
         * @param message
         */
        public ParameterPhoneNumberErrorException(final String message) {

            super(message);
        }

        /**
         *
         * @param message
         * @param cause
         */
        public ParameterPhoneNumberErrorException(final String message, final Throwable cause) {

            super(message, cause);
        }

        /**
         *
         * @param cause
         */
        public ParameterPhoneNumberErrorException(final Throwable cause) {

            super(cause);
        }
    }

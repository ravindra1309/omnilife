package com.omnilife.modules.finance.exception;

/**
 * Exception thrown when an account number already exists in the database.
 */
public class DuplicateAccountNumberException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Account number already exists";

    /**
     * Constructs a new DuplicateAccountNumberException with the default message.
     */
    public DuplicateAccountNumberException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new DuplicateAccountNumberException with the specified message.
     *
     * @param message the detail message
     */
    public DuplicateAccountNumberException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateAccountNumberException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public DuplicateAccountNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}



package com.omnilife.modules.finance.exception;

/**
 * Exception thrown when a ledger account cannot be found.
 */
public class AccountNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Account not found";

    /**
     * Constructs a new AccountNotFoundException with the default message.
     */
    public AccountNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new AccountNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public AccountNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new AccountNotFoundException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}



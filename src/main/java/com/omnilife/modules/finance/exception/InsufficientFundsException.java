package com.omnilife.modules.finance.exception;

/**
 * Exception thrown when an account has insufficient funds for a transaction.
 */
public class InsufficientFundsException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Insufficient balance for transaction";

    /**
     * Constructs a new InsufficientFundsException with the default message.
     */
    public InsufficientFundsException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new InsufficientFundsException with the specified message.
     *
     * @param message the detail message
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    /**
     * Constructs a new InsufficientFundsException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}


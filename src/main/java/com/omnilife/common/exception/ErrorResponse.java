package com.omnilife.common.exception;

import java.time.LocalDateTime;

/**
 * Record representing a standardized error response format.
 *
 * @param timestamp the timestamp when the error occurred
 * @param status    the HTTP status code
 * @param error     the error type or category
 * @param path      the request path where the error occurred
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String path
) {
}


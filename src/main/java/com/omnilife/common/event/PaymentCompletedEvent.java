package com.omnilife.common.event;

/**
 * Event record representing a payment that has been completed.
 *
 * @param orderId the ID of the order associated with the payment
 * @param userId  the ID of the user who made the payment
 */
public record PaymentCompletedEvent(
        String orderId,
        String userId
) {
}


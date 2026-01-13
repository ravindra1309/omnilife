package com.omnilife.common.event;

/**
 * Event record representing a payment that has failed.
 *
 * @param orderId the ID of the order associated with the failed payment
 * @param reason  the reason for the payment failure
 */
public record PaymentFailedEvent(
        String orderId,
        String reason
) {
}


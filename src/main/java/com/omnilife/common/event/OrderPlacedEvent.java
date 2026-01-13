package com.omnilife.common.event;

import java.math.BigDecimal;

/**
 * Event record representing an order that has been placed.
 *
 * @param orderId the ID of the order
 * @param userId  the ID of the user who placed the order
 * @param amount  the amount of the order
 */
public record OrderPlacedEvent(
        String orderId,
        String userId,
        BigDecimal amount
) {
}


package com.omnilife.modules.commerce.dto;

import java.math.BigDecimal;

/**
 * DTO representing a product view with stock quantity information.
 */
public record ProductView(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity
) {
}


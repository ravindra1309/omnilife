package com.omnilife.modules.commerce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.omnilife.modules.commerce.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for order summary information.
 */
public class OrderSummary {
    private Long id;
    private String productName;
    
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal amount;
    
    private LocalDateTime date;
    private OrderStatus status;

    public OrderSummary() {
    }

    public OrderSummary(Long id, String productName, BigDecimal amount, LocalDateTime date, OrderStatus status) {
        this.id = id;
        this.productName = productName;
        this.amount = amount;
        this.date = date;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}


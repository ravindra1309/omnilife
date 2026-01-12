package com.omnilife.modules.commerce.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omnilife.modules.commerce.domain.Order;
import com.omnilife.modules.commerce.dto.OrderSummary;
import com.omnilife.modules.commerce.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order operations.
 */
@RestController
@RequestMapping("/api/commerce")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order.
     *
     * @param request the order request containing userId and productId
     * @return the created Order
     */
    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest request) {
        Order order = orderService.placeOrder(request.getUserId(), request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId the user ID (account number) to retrieve orders for
     * @return a list of OrderSummary DTOs
     */
    @GetMapping("/orders/my")
    public ResponseEntity<List<OrderSummary>> getUserOrders(@RequestParam String userId) {
        List<OrderSummary> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * DTO for order creation request.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderRequest {
        @NotBlank(message = "User ID is required")
        private String userId;

        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Long productId;

        public OrderRequest() {
        }

        public OrderRequest(String userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }
    }
}



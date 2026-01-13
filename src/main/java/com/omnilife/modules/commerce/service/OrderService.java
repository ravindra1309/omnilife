package com.omnilife.modules.commerce.service;

import com.omnilife.common.event.OrderPlacedEvent;
import com.omnilife.modules.commerce.domain.Inventory;
import com.omnilife.modules.commerce.domain.Order;
import com.omnilife.modules.commerce.domain.OrderStatus;
import com.omnilife.modules.commerce.domain.Product;
import com.omnilife.modules.commerce.dto.OrderSummary;
import com.omnilife.modules.commerce.repository.InventoryRepository;
import com.omnilife.modules.commerce.repository.OrderRepository;
import com.omnilife.modules.commerce.repository.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for order operations in the commerce module.
 */
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(ProductRepository productRepository,
                       InventoryRepository inventoryRepository,
                       OrderRepository orderRepository,
                       ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Places an order for a product. This method is transactional to ensure
     * that if any step fails, all changes are rolled back.
     *
     * @param userId    the user ID (account number) placing the order
     * @param productId the product ID to order
     * @return the created Order
     * @throws RuntimeException if product is not found or out of stock
     */
    @Transactional
    public Order placeOrder(String userId, Long productId) {
        // Product Check: Find Product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Stock Check: Find Inventory
        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId));

        if (inventory.getQuantity() < 1) {
            throw new RuntimeException("Out of stock");
        }

        // Price Check: Get the price from Product
        BigDecimal price = product.getPrice();

        // Inventory Update: Decrement stock quantity - 1
        inventory.setQuantity(inventory.getQuantity() - 1);
        inventoryRepository.save(inventory);

        // Create Order with PENDING status
        Order order = Order.builder()
                .userId(userId)
                .product(product)
                .status(OrderStatus.PENDING)
                .amountPaid(price)
                .build();

        // Save the Order
        Order savedOrder = orderRepository.save(order);

        // Publish OrderPlacedEvent
        eventPublisher.publishEvent(new OrderPlacedEvent(
                String.valueOf(savedOrder.getId()),
                userId,
                price
        ));

        // Return the saved Order (it will be PENDING)
        return savedOrder;
    }

    /**
     * Retrieves all orders for a specific user as order summaries.
     *
     * @param userId the user ID (account number) to retrieve orders for
     * @return a list of OrderSummary DTOs
     */
    public List<OrderSummary> getUserOrders(String userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedDateDesc(userId);
        
        return orders.stream()
                .map(order -> {
                    // Look up the Product Name using the productId
                    Product product = order.getProduct();
                    String productName = product != null ? product.getName() : "Unknown Product";
                    
                    return new OrderSummary(
                            order.getId(),
                            productName,
                            order.getAmountPaid(),
                            order.getCreatedDate(),
                            order.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }
}



package com.omnilife.modules.commerce.service;

import com.omnilife.modules.commerce.domain.Inventory;
import com.omnilife.modules.commerce.domain.Order;
import com.omnilife.modules.commerce.domain.OrderStatus;
import com.omnilife.modules.commerce.domain.Product;
import com.omnilife.modules.commerce.repository.InventoryRepository;
import com.omnilife.modules.commerce.repository.OrderRepository;
import com.omnilife.modules.commerce.repository.ProductRepository;
import com.omnilife.modules.finance.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service class for order operations in the commerce module.
 */
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final WalletService walletService;

    public OrderService(ProductRepository productRepository,
                       InventoryRepository inventoryRepository,
                       OrderRepository orderRepository,
                       WalletService walletService) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.walletService = walletService;
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

        // Payment: Call walletService.debit(userId, price)
        walletService.debit(userId, price);

        // Inventory Update: Decrement stock quantity - 1
        inventory.setQuantity(inventory.getQuantity() - 1);
        inventoryRepository.save(inventory);

        // Create Order
        Order order = Order.builder()
                .userId(userId)
                .product(product)
                .status(OrderStatus.COMPLETED)
                .amountPaid(price)
                .build();

        // Save and Return Order
        return orderRepository.save(order);
    }
}


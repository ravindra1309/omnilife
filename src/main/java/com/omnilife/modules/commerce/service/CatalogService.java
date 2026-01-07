package com.omnilife.modules.commerce.service;

import com.omnilife.modules.commerce.domain.Inventory;
import com.omnilife.modules.commerce.domain.Product;
import com.omnilife.modules.commerce.dto.ProductView;
import com.omnilife.modules.commerce.repository.InventoryRepository;
import com.omnilife.modules.commerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for catalog operations including product listing and creation.
 */
@Service
public class CatalogService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public CatalogService(ProductRepository productRepository,
                         InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Retrieves all products with their inventory information.
     * For each product, fetches its inventory (defaults to 0 if missing) and maps to ProductView.
     *
     * @return a list of ProductView objects representing all products with stock quantities
     */
    public List<ProductView> getAllProducts() {
        List<Product> products = productRepository.findAll();
        
        return products.stream()
                .map(product -> {
                    int stockQuantity = inventoryRepository.findByProductId(product.getId())
                            .map(Inventory::getQuantity)
                            .orElse(0);
                    
                    return new ProductView(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getPrice(),
                            stockQuantity
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Creates a new product with inventory.
     * Creates and saves both the Product and its associated Inventory.
     *
     * @param name the product name
     * @param sku the product SKU (must be unique)
     * @param price the product price
     * @param stock the initial stock quantity
     * @return the saved Product entity
     */
    @Transactional
    public Product addProduct(String name, String sku, BigDecimal price, int stock) {
        // Create and save Product
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setPrice(price);
        Product savedProduct = productRepository.save(product);
        
        // Create and save Inventory linked to the product ID
        Inventory inventory = new Inventory();
        inventory.setProductId(savedProduct.getId());
        inventory.setQuantity(stock);
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryRepository.save(inventory);
        
        return savedProduct;
    }
}


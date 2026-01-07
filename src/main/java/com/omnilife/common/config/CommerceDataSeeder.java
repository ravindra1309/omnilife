package com.omnilife.common.config;

import com.omnilife.modules.commerce.service.CatalogService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Data seeder for commerce module.
 * Seeds initial product data if the database is empty.
 */
@Component
public class CommerceDataSeeder implements CommandLineRunner {

    private final CatalogService catalogService;

    public CommerceDataSeeder(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if the product list is empty
        if (catalogService.getAllProducts().isEmpty()) {
            // Seed initial products
            catalogService.addProduct("iPhone 16 Pro", "IPH-16", new BigDecimal("999.99"), 10);
            catalogService.addProduct("MacBook Air M3", "MAC-M3", new BigDecimal("1299.00"), 5);
            catalogService.addProduct("Sony WH-1000XM5", "SNY-HP", new BigDecimal("349.00"), 20);
        }
    }
}


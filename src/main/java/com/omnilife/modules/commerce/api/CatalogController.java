package com.omnilife.modules.commerce.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omnilife.modules.commerce.domain.Product;
import com.omnilife.modules.commerce.dto.ProductView;
import com.omnilife.modules.commerce.service.CatalogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for catalog operations.
 */
@RestController
@RequestMapping("/api/commerce")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Retrieves all products with their inventory information.
     *
     * @return a list of ProductView objects representing all products with stock quantities
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductView>> getAllProducts() {
        List<ProductView> products = catalogService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Creates a new product with inventory.
     *
     * @param request the request containing product details
     * @return the created Product entity
     */
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody AddProductRequest request) {
        Product product = catalogService.addProduct(
                request.getName(),
                request.getSku(),
                request.getPrice(),
                request.getStock()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * DTO for product creation request.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddProductRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "SKU is required")
        private String sku;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        private BigDecimal price;

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock must be non-negative")
        private Integer stock;

        public AddProductRequest() {
        }

        public AddProductRequest(String name, String sku, BigDecimal price, Integer stock) {
            this.name = name;
            this.sku = sku;
            this.price = price;
            this.stock = stock;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }
}


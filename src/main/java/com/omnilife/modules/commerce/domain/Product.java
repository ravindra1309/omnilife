package com.omnilife.modules.commerce.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * JPA Entity representing a product in the commerce module.
 */
@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku", unique = true, nullable = false)
    private String sku;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;
}


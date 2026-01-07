package com.omnilife.modules.commerce.repository;

import com.omnilife.modules.commerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity operations.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}


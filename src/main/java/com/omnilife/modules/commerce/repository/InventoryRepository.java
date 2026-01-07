package com.omnilife.modules.commerce.repository;

import com.omnilife.modules.commerce.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Inventory entity operations.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Finds inventory by product ID.
     *
     * @param productId the product ID to search for
     * @return an Optional containing the Inventory if found, empty otherwise
     */
    Optional<Inventory> findByProductId(Long productId);
}


package com.omnilife.modules.commerce.repository;

import com.omnilife.modules.commerce.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order entity operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Finds all orders for a specific user, ordered by creation date in descending order (most recent first).
     *
     * @param userId the user ID to search for
     * @return a list of Order entities for the user, sorted by created date descending
     */
    List<Order> findByUserIdOrderByCreatedDateDesc(String userId);
}



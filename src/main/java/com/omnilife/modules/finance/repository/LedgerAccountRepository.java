package com.omnilife.modules.finance.repository;

import com.omnilife.modules.finance.domain.LedgerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for LedgerAccount entity operations.
 */
@Repository
public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, Long> {

    /**
     * Finds a ledger account by its unique account number.
     *
     * @param accountNumber the account number to search for
     * @return an Optional containing the LedgerAccount if found, empty otherwise
     */
    Optional<LedgerAccount> findByAccountNumber(String accountNumber);
}


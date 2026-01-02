package com.omnilife.modules.finance.repository;

import com.omnilife.modules.finance.domain.LedgerAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Finds a ledger account by its unique account number with pessimistic write lock.
     * This method acquires a database-level lock on the account row to prevent concurrent modifications
     * during transactions, ensuring data consistency and preventing race conditions.
     *
     * @param accountNumber the account number to search for
     * @return an Optional containing the LedgerAccount if found, empty otherwise
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM LedgerAccount a WHERE a.accountNumber = :accountNumber")
    Optional<LedgerAccount> findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);
}



package com.omnilife.modules.finance.repository;

import com.omnilife.modules.finance.domain.JournalEntry;
import com.omnilife.modules.finance.domain.LedgerAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for JournalEntry entity operations.
 */
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    /**
     * Finds all journal entries associated with a specific transaction ID.
     * Multiple entries can share the same transaction ID (e.g., DEBIT and CREDIT entries
     * for the same transfer).
     *
     * @param transactionId the transaction ID to search for
     * @return a list of JournalEntry entities with the given transaction ID
     */
    List<JournalEntry> findByTransactionId(String transactionId);

    /**
     * Finds all journal entries for a specific account, sorted by timestamp in descending order
     * (most recent first), with pagination support.
     *
     * @param account the LedgerAccount to find entries for
     * @param pageable pagination information (page number, size, and sort)
     * @return a page of JournalEntry entities for the account, sorted by timestamp descending
     */
    Page<JournalEntry> findByAccountOrderByTimestampDesc(LedgerAccount account, Pageable pageable);
}



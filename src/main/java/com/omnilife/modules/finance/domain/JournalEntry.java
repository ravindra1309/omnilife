package com.omnilife.modules.finance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a journal entry in the finance module.
 * Journal entries record all financial transactions and maintain double-entry bookkeeping.
 * 
 * Note: Amount validation (ensuring amounts are always positive) should be implemented
 * in the service layer when creating or updating journal entries.
 */
@Entity
@Table(name = "journal_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, length = 100)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull
    private LedgerAccount account;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    @NotNull
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    @NotNull
    private JournalEntryType type;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        // Note: Amount validation (ensuring amounts are always positive) should be
        // implemented in the service layer when creating or updating journal entries.
    }
}


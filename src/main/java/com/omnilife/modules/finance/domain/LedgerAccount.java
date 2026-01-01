package com.omnilife.modules.finance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a ledger account in the finance module.
 */
@Entity
@Table(name = "ledger_accounts", uniqueConstraints = {
    @UniqueConstraint(name = "uk_account_number", columnNames = "account_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, updatable = false, length = 10)
    private String accountNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LedgerAccountStatus status;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        if (currency == null || currency.isEmpty()) {
            currency = "USD";
        }
        // Validate account number format if set
        if (accountNumber != null && (!accountNumber.matches("^2026\\d{6}$") || accountNumber.length() != 10)) {
            throw new IllegalStateException("Invalid account number format. Expected 10 digits starting with '2026', got: " + accountNumber);
        }
        // Note: Account number generation is handled in the service layer
        // to ensure uniqueness by checking against existing accounts
    }
}


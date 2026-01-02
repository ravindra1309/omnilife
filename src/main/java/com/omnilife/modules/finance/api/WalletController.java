package com.omnilife.modules.finance.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.omnilife.modules.finance.domain.JournalEntryType;
import com.omnilife.modules.finance.domain.LedgerAccount;
import com.omnilife.modules.finance.service.WalletService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for wallet operations.
 */
@RestController
@RequestMapping("/api/finance")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Creates a new wallet.
     *
     * @param request the request containing name and currency
     * @return the created LedgerAccount
     */
    @PostMapping("/wallets")
    public ResponseEntity<LedgerAccount> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        LedgerAccount account = walletService.createWallet(request.getName(), request.getCurrency());
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    /**
     * Deposits funds into a wallet.
     *
     * @param accountNumber the account number
     * @param request       the request containing the deposit amount
     * @return the updated LedgerAccount
     */
    @PostMapping("/wallets/{accountNumber}/deposit")
    public ResponseEntity<LedgerAccount> deposit(@PathVariable String accountNumber,
                                                 @Valid @RequestBody DepositRequest request) {
        LedgerAccount account = walletService.fundWallet(accountNumber, request.getAmount());
        return ResponseEntity.ok(account);
    }

    /**
     * Transfers funds between two accounts.
     *
     * @param request the transfer request containing fromUser, toUser, and amount
     * @return a success message
     */
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@Valid @RequestBody TransferRequest request) {
        walletService.transferFunds(request.getFromUser(), request.getToUser(), request.getAmount());
        return ResponseEntity.ok(Map.of("message", "Transfer successful"));
    }

    /**
     * Retrieves the transaction history for a specific wallet account.
     *
     * @param accountNumber the account number to retrieve transaction history for
     * @return a list of TransactionHistoryDto objects representing the account's transaction history
     */
    @GetMapping("/wallets/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory(@PathVariable String accountNumber) {
        List<TransactionHistoryDto> history = walletService.getAccountHistory(accountNumber);
        return ResponseEntity.ok(history);
    }

    /**
     * DTO for wallet creation request.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateWalletRequest {
        @NotBlank(message = "Name is required")
        private String name;
        
        private String currency;

        public CreateWalletRequest() {
        }

        public CreateWalletRequest(String name, String currency) {
            this.name = name;
            this.currency = currency;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    /**
     * DTO for deposit request.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DepositRequest {
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private BigDecimal amount;

        public DepositRequest() {
        }

        public DepositRequest(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    /**
     * DTO for transfer request.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransferRequest {
        @NotBlank(message = "From user account number is required")
        private String fromUser;
        
        @NotBlank(message = "To user account number is required")
        private String toUser;
        
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private BigDecimal amount;

        public TransferRequest() {
        }

        public TransferRequest(String fromUser, String toUser, BigDecimal amount) {
            this.fromUser = fromUser;
            this.toUser = toUser;
            this.amount = amount;
        }

        public String getFromUser() {
            return fromUser;
        }

        public void setFromUser(String fromUser) {
            this.fromUser = fromUser;
        }

        public String getToUser() {
            return toUser;
        }

        public void setToUser(String toUser) {
            this.toUser = toUser;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    /**
     * DTO for transaction history response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionHistoryDto {
        private String transactionId;
        private JournalEntryType type;
        private BigDecimal amount;
        private String currency;
        private LocalDateTime timestamp;
        private String description;

        public TransactionHistoryDto() {
        }

        public TransactionHistoryDto(String transactionId, JournalEntryType type, BigDecimal amount,
                                     String currency, LocalDateTime timestamp, String description) {
            this.transactionId = transactionId;
            this.type = type;
            this.amount = amount;
            this.currency = currency;
            this.timestamp = timestamp;
            this.description = description;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public JournalEntryType getType() {
            return type;
        }

        public void setType(JournalEntryType type) {
            this.type = type;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}


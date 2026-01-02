package com.omnilife.modules.finance.service;

import com.omnilife.modules.finance.api.WalletController;
import com.omnilife.modules.finance.domain.JournalEntry;
import com.omnilife.modules.finance.domain.JournalEntryType;
import com.omnilife.modules.finance.domain.LedgerAccount;
import com.omnilife.modules.finance.domain.LedgerAccountStatus;
import com.omnilife.modules.finance.exception.AccountNotFoundException;
import com.omnilife.modules.finance.exception.DuplicateAccountNumberException;
import com.omnilife.modules.finance.exception.InsufficientFundsException;
import com.omnilife.modules.finance.repository.JournalEntryRepository;
import com.omnilife.modules.finance.repository.LedgerAccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for wallet operations including account creation, funding, and transfers.
 */
@Service
public class WalletService {

    private final LedgerAccountRepository ledgerAccountRepository;
    private final JournalEntryRepository journalEntryRepository;

    public WalletService(LedgerAccountRepository ledgerAccountRepository,
                         JournalEntryRepository journalEntryRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
        this.journalEntryRepository = journalEntryRepository;
    }

    /**
     * Generates a unique 10-digit account number starting with "2026".
     * Format: 2026XXXXXX where XXXXXX is a 6-digit random number.
     *
     * @return a unique account number
     */
    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;
        int maxAttempts = 100; // Prevent infinite loop
        int attempts = 0;

        do {
            // Generate 6 random digits (000000 to 999999)
            int randomDigits = random.nextInt(1000000);
            accountNumber = String.format("2026%06d", randomDigits);
            attempts++;

            // Check if account number already exists
            if (!ledgerAccountRepository.findByAccountNumber(accountNumber).isPresent()) {
                return accountNumber;
            }
        } while (attempts < maxAttempts);

        // If we couldn't find a unique number after max attempts, throw exception
        throw new IllegalStateException("Unable to generate unique account number after " + maxAttempts + " attempts");
    }

    /**
     * Creates a new wallet (LedgerAccount).
     * Handles race conditions by retrying with a new account number if a duplicate is detected.
     *
     * @param name     the name of the wallet owner
     * @param currency the currency code (e.g., "USD")
     * @return the created and saved LedgerAccount
     * @throws DuplicateAccountNumberException if unable to generate a unique account number after retries
     */
    @Transactional
    public LedgerAccount createWallet(String name, String currency) {
        int maxRetries = 5;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                // Generate unique 10-digit account number starting with "2026"
                String accountNumber = generateAccountNumber();
                
                // Ensure account number is not null and has correct format
                if (accountNumber == null || accountNumber.length() != 10 || !accountNumber.startsWith("2026")) {
                    throw new IllegalStateException("Invalid account number generated: " + accountNumber);
                }

                // Verify account number doesn't exist (double-check before save)
                if (ledgerAccountRepository.findByAccountNumber(accountNumber).isPresent()) {
                    retryCount++;
                    continue; // Retry with a new account number
                }

                LedgerAccount account = LedgerAccount.builder()
                        .name(name)
                        .currency(currency != null && !currency.isEmpty() ? currency : "USD")
                        .balance(BigDecimal.ZERO)
                        .accountNumber(accountNumber)
                        .status(LedgerAccountStatus.ACTIVE)
                        .build();

                // Double-check account number is set correctly
                if (account.getAccountNumber() == null || !account.getAccountNumber().equals(accountNumber)) {
                    account.setAccountNumber(accountNumber);
                }

                // Attempt to save - this will throw DataIntegrityViolationException if duplicate exists
                return ledgerAccountRepository.save(account);

            } catch (DataIntegrityViolationException e) {
                // Handle unique constraint violation (race condition)
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new DuplicateAccountNumberException(
                            "Unable to create wallet: account number already exists. Please try again.", e);
                }
                // Continue to retry with a new account number
            }
        }

        throw new DuplicateAccountNumberException(
                "Unable to create wallet: failed to generate unique account number after " + maxRetries + " attempts");
    }

    /**
     * Funds a wallet by adding money to the account balance.
     * This is a simple method for testing/seeding money without full double-entry bookkeeping.
     *
     * @param accountNumber the account number to fund
     * @param amount        the amount to add to the balance
     * @return the updated LedgerAccount
     * @throws AccountNotFoundException if the account is not found
     */
    public LedgerAccount fundWallet(String accountNumber, BigDecimal amount) {
        LedgerAccount account = ledgerAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        account.setBalance(account.getBalance().add(amount));
        return ledgerAccountRepository.save(account);
    }

    /**
     * Transfers funds from one account to another with full double-entry bookkeeping.
     * This method is transactional to ensure data consistency.
     *
     * @param fromAccountNum the account number to transfer from
     * @param toAccountNum   the account number to transfer to
     * @param amount         the amount to transfer
     * @throws AccountNotFoundException  if either account is not found
     * @throws InsufficientFundsException if the fromAccount has insufficient balance
     * @throws IllegalArgumentException   if the amount is not positive
     */
    @Transactional
    public void transferFunds(String fromAccountNum, String toAccountNum, BigDecimal amount) {
        // Validation: Check if amount is positive
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        // Validation: Check if both accounts exist
        // Using pessimistic write lock to prevent race conditions and double spending.
        // This ensures that when multiple concurrent transfer requests target the same account,
        // only one transaction can modify the account balance at a time, preventing:
        // - Double spending (same funds being transferred twice)
        // - Lost updates (concurrent balance modifications overwriting each other)
        // - Negative balances (insufficient balance checks passing due to race conditions)
        LedgerAccount fromAccount = ledgerAccountRepository.findByAccountNumberWithLock(fromAccountNum)
                .orElseThrow(() -> new AccountNotFoundException("From account not found: " + fromAccountNum));

        LedgerAccount toAccount = ledgerAccountRepository.findByAccountNumberWithLock(toAccountNum)
                .orElseThrow(() -> new AccountNotFoundException("To account not found: " + toAccountNum));

        // Validation: Check if fromAccount has sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    String.format("Insufficient balance. Current balance: %s, Required: %s",
                            fromAccount.getBalance(), amount));
        }

        // Execution: Deduct amount from fromAccount
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));

        // Execution: Add amount to toAccount
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Save both accounts
        ledgerAccountRepository.save(fromAccount);
        ledgerAccountRepository.save(toAccount);

        // Create transaction ID for linking both journal entries
        String transactionId = UUID.randomUUID().toString();

        // Create DEBIT entry for fromAccount
        JournalEntry debitEntry = JournalEntry.builder()
                .transactionId(transactionId)
                .account(fromAccount)
                .amount(amount)
                .type(JournalEntryType.DEBIT)
                .description(String.format("Transfer to account %s", toAccountNum))
                .build();

        // Create CREDIT entry for toAccount
        JournalEntry creditEntry = JournalEntry.builder()
                .transactionId(transactionId)
                .account(toAccount)
                .amount(amount)
                .type(JournalEntryType.CREDIT)
                .description(String.format("Transfer from account %s", fromAccountNum))
                .build();

        // Save both journal entries
        journalEntryRepository.save(debitEntry);
        journalEntryRepository.save(creditEntry);
    }

    /**
     * Retrieves the transaction history for a specific account.
     * Returns all journal entries for the account, sorted by timestamp in descending order
     * (most recent first).
     *
     * @param accountNumber the account number to retrieve history for
     * @return a list of TransactionHistoryDto objects representing the account's transaction history
     * @throws AccountNotFoundException if the account is not found
     */
    public List<WalletController.TransactionHistoryDto> getAccountHistory(String accountNumber) {
        // Find the account first to ensure it exists
        LedgerAccount account = ledgerAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        // Fetch all journal entries for this account, sorted by timestamp descending
        List<JournalEntry> entries = journalEntryRepository.findByAccountOrderByTimestampDesc(account);

        // Map journal entries to DTOs
        return entries.stream()
                .map(entry -> new WalletController.TransactionHistoryDto(
                        entry.getTransactionId(),
                        entry.getType(),
                        entry.getAmount(),
                        account.getCurrency(),
                        entry.getTimestamp(),
                        entry.getDescription()
                ))
                .collect(Collectors.toList());
    }
}


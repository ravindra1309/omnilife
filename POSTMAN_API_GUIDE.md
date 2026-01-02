# Postman API Testing Guide

## Base URL
```
http://localhost:8080
```

## 1. Create Wallet

**Request:**
- Method: `POST`
- URL: `http://localhost:8080/api/finance/wallets`
- Headers:
  ```
  Content-Type: application/json
  ```
- Body (raw JSON):
  ```json
  {
    "name": "John Doe",
    "currency": "USD"
  }
  ```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "accountNumber": "2026123456",
  "name": "John Doe",
  "balance": 0,
  "currency": "USD",
  "status": "ACTIVE",
  "createdDate": "2026-01-01T12:00:00"
}
```

**Note:** Save the `accountNumber` from the response for use in other endpoints.

---

## 2. Deposit Funds

**Request:**
- Method: `POST`
- URL: `http://localhost:8080/api/finance/wallets/{accountNumber}/deposit`
  - Replace `{accountNumber}` with actual account number (e.g., `2026123456`)
- Headers:
  ```
  Content-Type: application/json
  ```
- Body (raw JSON):
  ```json
  {
    "amount": 100.50
  }
  ```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "accountNumber": "2026123456",
  "name": "John Doe",
  "balance": 100.50,
  "currency": "USD",
  "status": "ACTIVE",
  "createdDate": "2026-01-01T12:00:00"
}
```

---

## 3. Transfer Funds

**Request:**
- Method: `POST`
- URL: `http://localhost:8080/api/finance/transfer`
- Headers:
  ```
  Content-Type: application/json
  ```
- Body (raw JSON):
  ```json
  {
    "fromUser": "2026123456",
    "toUser": "2026789012",
    "amount": 50.00
  }
  ```

**Expected Response (200 OK):**
```json
{
  "message": "Transfer successful"
}
```

---

## 4. Get Transaction History

**Request:**
- Method: `GET`
- URL: `http://localhost:8080/api/finance/wallets/{accountNumber}/transactions`
  - Replace `{accountNumber}` with actual account number (e.g., `2026123456`)

**Expected Response (200 OK):**
```json
[
  {
    "transactionId": "550e8400-e29b-41d4-a716-446655440000",
    "type": "DEBIT",
    "amount": 50.00,
    "currency": "USD",
    "timestamp": "2026-01-01T12:05:00",
    "description": "Transfer to account 2026789012"
  },
  {
    "transactionId": "550e8400-e29b-41d4-a716-446655440000",
    "type": "CREDIT",
    "amount": 50.00,
    "currency": "USD",
    "timestamp": "2026-01-01T12:05:00",
    "description": "Transfer from account 2026123456"
  },
  {
    "transactionId": "660e8400-e29b-41d4-a716-446655440001",
    "type": "DEBIT",
    "amount": 25.00,
    "currency": "USD",
    "timestamp": "2026-01-01T12:03:00",
    "description": "Transfer to account 2026789012"
  }
]
```

**Note:** Transactions are returned in descending order (most recent first). Each transfer creates two entries: a DEBIT for the sender and a CREDIT for the receiver, linked by the same `transactionId`.

---

## Error Responses

### Validation Error (400 Bad Request)
```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "fieldErrors": {
    "name": "Name is required"
  }
}
```

### Account Not Found (404 Not Found)
```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 404,
  "error": "Account Not Found",
  "message": "Account not found: 2026999999"
}
```

### Insufficient Funds (400 Bad Request)
```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 400,
  "error": "Insufficient Funds",
  "message": "Insufficient balance. Current balance: 10.00, Required: 50.00"
}
```

---

## Complete Test Flow

1. **Create Wallet 1:**
   - POST `/api/finance/wallets`
   - Body: `{"name": "Alice", "currency": "USD"}`
   - Save `accountNumber` (e.g., `2026123456`)

2. **Create Wallet 2:**
   - POST `/api/finance/wallets`
   - Body: `{"name": "Bob", "currency": "USD"}`
   - Save `accountNumber` (e.g., `2026789012`)

3. **Deposit to Wallet 1:**
   - POST `/api/finance/wallets/2026123456/deposit`
   - Body: `{"amount": 200.00}`

4. **Transfer from Wallet 1 to Wallet 2:**
   - POST `/api/finance/transfer`
   - Body: `{"fromUser": "2026123456", "toUser": "2026789012", "amount": 75.00}`

5. **Verify Balances:**
   - Check Wallet 1 balance should be: 125.00
   - Check Wallet 2 balance should be: 75.00

6. **View Transaction History for Wallet 1:**
   - GET `/api/finance/wallets/2026123456/transactions`
   - Should show DEBIT entries for transfers made from this account

7. **View Transaction History for Wallet 2:**
   - GET `/api/finance/wallets/2026789012/transactions`
   - Should show CREDIT entries for transfers received to this account



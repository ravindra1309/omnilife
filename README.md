# OmniLife - Enterprise Core Banking & Commerce Super App

OmniLife is a modular monolith application built with **Java 21** and **Spring Boot 3.3**, demonstrating enterprise-grade architecture. It integrates a **Double-Entry Ledger System** (Finance) with an **Inventory-Aware Marketplace** (Commerce), decoupled using **Event-Driven Architecture (Saga Pattern)**.

---

## 🚀 Key Architectural Features

### 1. Spring Modulith
The application is structured into strictly defined modules (`finance`, `commerce`) that do not directly depend on each other's internal services.
* **Benefits:** independent deployability potential, clean classpath, and strictly enforced boundaries.

### 2. Event-Driven Saga Pattern (Orchestration/Choreography)
Instead of synchronous HTTP/Service calls (which create tight coupling), OmniLife uses **Spring Application Events** to handle distributed transactions.

**The "Buy Now" Flow:**
1.  **Commerce:** User places order $\rightarrow$ Order saved as `PENDING` $\rightarrow$ Publishes `OrderPlacedEvent`.
2.  **Finance:** Listens for event $\rightarrow$ Validates funds $\rightarrow$ Debits Wallet (ACID transaction) $\rightarrow$ Publishes `PaymentCompletedEvent` (or `PaymentFailedEvent`).
3.  **Commerce:** Listens for result $\rightarrow$ Updates Order to `COMPLETED` or `FAILED` $\rightarrow$ Updates Stock.



### 3. Concurrency & Data Integrity
* **Pessimistic Locking:** `SELECT ... FOR UPDATE` used in `WalletService` to prevent double-spending during concurrent transfers.
* **Double-Entry Ledger:** Every financial movement records a Debit and a Credit log to ensure auditability (`SUM(debits) == SUM(credits)`).

---

## 🛠 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.3, Spring Data JPA, Spring Modulith
* **Database:** MySQL 8
* **Testing:** JUnit 5, Mockito
* **Tools:** Maven, Lombok, Docker

---

## 📦 Module Breakdown

### 💰 Finance Module
Handles all money movements, wallets, and audit trails.
* **Key Service:** `WalletService` (Handles Transfers & Debits).
* **Listener:** `FinanceEventListener` (Async listener for Commerce events).
* **Security:** Pessimistic Locking on Account entities.

### 🛍️ Commerce Module
Handles product catalog, inventory, and order lifecycle.
* **Key Service:** `OrderService` (Manages Order State).
* **Listener:** `CommerceEventListener` (Reacts to Payment success/failure).
* **Data Model:** Separate `Product` (Static) and `Inventory` (Dynamic) tables.

---

## 🧪 How to Run

1.  **Prerequisites:**
    * Java 21 SDK
    * MySQL Running (Port 3306)
    * Create Database: `CREATE DATABASE omnilife;`

2.  **Start the App:**
    ```bash
    mvn spring-boot:run
    ```
    *Data Seeding:* The app automatically creates "Alice" (Wallet) and stocks the catalog (iPhone, Mac) on startup.

3.  **Test the Saga Flow (cURL / Postman):**
    * **Step 1:** Check Products
        `GET http://localhost:8080/api/commerce/products`
    * **Step 2:** Place Order (Trigger Event Chain)
        ```bash
        POST http://localhost:8080/api/commerce/orders
        Content-Type: application/json
        {
          "userId": "ACC-UUID-ALICE...",
          "productId": 1
        }
        ```
    * **Step 3:** Verify Status
        `GET http://localhost:8080/api/commerce/orders/my?userId=ACC-UUID-ALICE...`
        *(Status should be COMPLETED)*

---

## 🔮 Future Roadmap
* [ ] Extract Modules into Microservices (Kafka).
* [ ] Add JWT Authentication (Spring Security).
* [ ] Implement "Buy Now, Pay Later" (Credit Logic).

---
*Built by [Your Name] as a demonstration of high-scale financial systems engineering.*

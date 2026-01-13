package com.omnilife.modules.finance.service;

import com.omnilife.common.event.OrderPlacedEvent;
import com.omnilife.common.event.PaymentCompletedEvent;
import com.omnilife.common.event.PaymentFailedEvent;
import com.omnilife.modules.finance.exception.InsufficientFundsException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener for finance-related events.
 * Handles order placement events and processes payments.
 */
@Component
public class FinanceEventListener {

    private final WalletService walletService;
    private final ApplicationEventPublisher eventPublisher;

    public FinanceEventListener(WalletService walletService,
                               ApplicationEventPublisher eventPublisher) {
        this.walletService = walletService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Listens for OrderPlacedEvent and processes the payment.
     * If payment succeeds, publishes PaymentCompletedEvent.
     * If payment fails due to insufficient funds, publishes PaymentFailedEvent.
     *
     * @param event the OrderPlacedEvent
     */
    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        try {
            // Try to debit the wallet
            walletService.debit(event.userId(), event.amount());

            // If successful, publish PaymentCompletedEvent
            eventPublisher.publishEvent(new PaymentCompletedEvent(
                    event.orderId(),
                    event.userId()
            ));
        } catch (InsufficientFundsException e) {
            // If insufficient funds, publish PaymentFailedEvent
            eventPublisher.publishEvent(new PaymentFailedEvent(
                    event.orderId(),
                    "Insufficient Funds"
            ));
        }
    }
}


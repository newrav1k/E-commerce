package ru.mirea.newrav1k.paymentservice.controller.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.mirea.newrav1k.paymentservice.controller.kafka.producer.PaymentCommandProducer;
import ru.mirea.newrav1k.paymentservice.exception.BankAccountNotFound;
import ru.mirea.newrav1k.paymentservice.exception.InsufficientFundsException;
import ru.mirea.newrav1k.paymentservice.service.BankAccountService;
import ru.mirea.newrav1k.paymentservice.service.IdempotencyService;
import ru.newrav1k.mirea.core.model.event.SagaOrderCancelledEvent;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationSuccessEvent;

@Slf4j
@Component
@KafkaListener(topics = {
        "${payment-service.kafka.topics.product-reserved}",
        "${payment-service.kafka.topics.order-cancelled}"
}, groupId = "${payment-service.kafka.group-id}")
@RequiredArgsConstructor
public class PaymentCommandConsumer {

    private final PaymentCommandProducer paymentCommandProducer;

    private final BankAccountService bankAccountService;

    private final IdempotencyService idempotencyService;

    @RetryableTopic(
            include = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            },
            exclude = {
                    BankAccountNotFound.class,
                    InsufficientFundsException.class
            }
    )
    @KafkaHandler
    public void processProductReserved(@Payload SagaProductReservationSuccessEvent event) {
        log.info("Received product reservation success event: {}", event);
        if (Boolean.TRUE.equals(this.idempotencyService.isProcessed(event.eventId()))) {
            log.info("Processing product already processed. Skipping product reservation.");
            return;
        }

        try {
            handleReservation(event);

            this.idempotencyService.markProcessed(event.eventId());
        } catch (InsufficientFundsException exception) {
            log.error("Insufficient funds to process product reservation success event : {}", event.orderId(), exception);
            this.paymentCommandProducer.processFailurePayment(event.orderId(), event.customerId(), event.items(), "Insufficient funds");
        } catch (Exception exception) {
            log.error("Error processing product reservation success event : {}", event.orderId(), exception);
            handlePaymentFailure(event, "Error while processing product reservation");
        }
    }

    @RetryableTopic(
            include = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            },
            exclude = {
                    BankAccountNotFound.class
            }
    )
    @KafkaHandler
    public void processOrderCancelled(@Payload SagaOrderCancelledEvent event) {
        log.info("Processing order cancelled: {}", event);
        if (Boolean.TRUE.equals(this.idempotencyService.isProcessed(event.eventId()))) {
            log.warn("Processing order already processed. Skipping processing order cancelled.");
            return;
        }

        this.bankAccountService.depositAmount(event.customerId(), event.orderId(), event.total());
        this.paymentCommandProducer.processFailurePayment(event.orderId(), event.customerId(), event.products(), "Order cancelled");
        this.idempotencyService.markProcessed(event.eventId());
    }

    private void handleReservation(SagaProductReservationSuccessEvent event) {
        try {
            this.bankAccountService.substanceAmount(event.customerId(), event.orderId(), event.total());
        } catch (InsufficientFundsException exception) {
            log.error("Failed to subtract funds to reservation. Skipping processing reservation.");
            throw exception;
        }
        this.paymentCommandProducer.processSuccessPayment(event.orderId(), event.customerId(), event.total());
    }

    private void handlePaymentFailure(SagaProductReservationSuccessEvent event, String reason) {
        // TODO: исправить возврат средств, если их не было изначально
        this.bankAccountService.depositAmount(event.customerId(), event.orderId(), event.total());
        this.paymentCommandProducer.processFailurePayment(event.orderId(), event.customerId(), event.items(), reason);
    }

}
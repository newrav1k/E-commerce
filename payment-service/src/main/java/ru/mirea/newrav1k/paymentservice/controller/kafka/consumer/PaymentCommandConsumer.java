package ru.mirea.newrav1k.paymentservice.controller.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.mirea.newrav1k.paymentservice.controller.kafka.producer.PaymentCommandProducer;
import ru.mirea.newrav1k.paymentservice.service.BankAccountService;
import ru.newrav1k.mirea.core.model.event.SagaCreationCancelledEvent;
import ru.newrav1k.mirea.core.model.event.SagaPaymentProcessingEvent;

@Slf4j
@Component
@KafkaListener(topics = {
        "${payment-service.kafka.topics.payment-process}",
        "${payment-service.kafka.topics.order-cancelled}"
}, groupId = "${payment-service.kafka.group-id}")
@RequiredArgsConstructor
public class PaymentCommandConsumer {

    private final PaymentCommandProducer paymentCommandProducer;

    private final BankAccountService bankAccountService;

    @KafkaHandler
    public void processPayment(@Payload SagaPaymentProcessingEvent event) {
        log.warn("Processing payment: {}", event);
        this.bankAccountService.substanceAmount(event.customerId(), event.orderId(), event.total());

        this.paymentCommandProducer.processSuccessPayment(event.customerId(), event.orderId(), event.total());
    }

    @KafkaHandler
    public void processOrderCancelled(@Payload SagaCreationCancelledEvent event) {
        log.warn("Processing order cancelled: {}", event);
        this.bankAccountService.depositAmount(event.customerId(), event.orderId(), event.total());

        this.paymentCommandProducer.processFailurePayment(event.customerId(), event.orderId(), event.total());
    }

}
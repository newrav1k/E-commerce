package ru.mirea.newrav1k.paymentservice.controller.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.newrav1k.mirea.core.model.event.SagaPaymentFailureEvent;
import ru.newrav1k.mirea.core.model.event.SagaPaymentSuccessEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCommandProducer {

    @Value("${payment-service.kafka.topics.payment-processed}")
    private String successPaymentProcessedTopic;

    @Value("${payment-service.kafka.topics.payment-failed}")
    private String failurePaymentFailedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void processSuccessPayment(UUID orderId, UUID customerId, BigDecimal total) {
        log.info("Processing payment success topic {}", this.successPaymentProcessedTopic);
        SagaPaymentSuccessEvent event = new SagaPaymentSuccessEvent(orderId, customerId, total);
        this.kafkaTemplate.send(this.successPaymentProcessedTopic, event);
    }

    public void processFailurePayment(UUID orderId, UUID customerId, BigDecimal total) {
        log.info("Processing payment failure topic {}", this.failurePaymentFailedTopic);
        SagaPaymentFailureEvent event = new SagaPaymentFailureEvent(orderId, customerId, total);
        this.kafkaTemplate.send(this.failurePaymentFailedTopic, event);
    }

}
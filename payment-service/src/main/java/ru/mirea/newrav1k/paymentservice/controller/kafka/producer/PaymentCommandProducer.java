package ru.mirea.newrav1k.paymentservice.controller.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.newrav1k.mirea.core.model.event.SagaPaymentFailureEvent;
import ru.newrav1k.mirea.core.model.event.SagaPaymentSuccessEvent;
import ru.newrav1k.mirea.core.model.payload.ItemInformation;

import java.math.BigDecimal;
import java.util.List;
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
        SagaPaymentSuccessEvent event = new SagaPaymentSuccessEvent(UUID.randomUUID(), orderId, customerId, total);
        this.kafkaTemplate.send(this.successPaymentProcessedTopic, event);
    }

    public void processFailurePayment(UUID orderId, UUID customerId, List<ItemInformation> products, String reason) {
        log.info("Processing payment failure topic {}", this.failurePaymentFailedTopic);
        SagaPaymentFailureEvent event = new SagaPaymentFailureEvent(UUID.randomUUID(), orderId, customerId, products, reason);
        this.kafkaTemplate.send(this.failurePaymentFailedTopic, event);
    }

}
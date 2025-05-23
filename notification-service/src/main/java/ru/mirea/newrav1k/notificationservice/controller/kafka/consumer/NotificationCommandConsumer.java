package ru.mirea.newrav1k.notificationservice.controller.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.mail.MailSendException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import ru.mirea.newrav1k.notificationservice.service.IdempotencyService;
import ru.mirea.newrav1k.notificationservice.service.NotificationService;
import ru.newrav1k.mirea.core.model.event.SagaOrderConfirmedEvent;

@Slf4j
@Component
@KafkaListener(topics = {
        "${notification-service.kafka.topics.order-confirmed}"
}, groupId = "${spring.application.name}")
@RequiredArgsConstructor
public class NotificationCommandConsumer {

    private final NotificationService notificationService;

    private final IdempotencyService idempotencyService;

    @RetryableTopic(include = {
            MailSendException.class
    }, backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaHandler
    public void handleOrderConfirmed(@Payload SagaOrderConfirmedEvent event) {
        if (Boolean.TRUE.equals(this.idempotencyService.isProcessed(event.eventId()))) {
            log.warn("Order confirmed, skipping processing of event: {}", event);
            return;
        }
        log.info("Received order confirmed");
        this.notificationService.sendOrderConfirmedNotification(event);
    }

}
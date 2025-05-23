package ru.mirea.newrav1k.notificationservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.mirea.newrav1k.notificationservice.service.client.UserClient;
import ru.newrav1k.mirea.core.model.event.SagaOrderConfirmedEvent;
import ru.newrav1k.mirea.core.model.payload.UserResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    private final UserClient userClient;

    private final IdempotencyService idempotencyService;

    @Async
    @Retryable(retryFor = {
            MailSendException.class,
            FeignException.class
    }, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendOrderConfirmedNotification(SagaOrderConfirmedEvent event) {
        log.info("Sending order confirmed notification");

        UserResponse response = this.userClient.findByCustomerId(event.customerId());

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(response.username());
        message.setSubject("Your Order is Confirmed!");
        message.setText("Dear customer, your order has been successfully confirmed.");

        this.mailSender.send(message);
        this.idempotencyService.markProcessed(event.eventId());
    }

    @Recover
    public void handleFeignException(FeignException exception, SagaOrderConfirmedEvent event) {
        log.error("Handling feign exception while request for user: {}", event.customerId());
        // TODO: обработку ошибки при запросе
    }

    @Recover
    public void handleMailSendException(MailSendException exception, SagaOrderConfirmedEvent event) {
        log.error("Mail send exception while sending order confirmed notification to: {}", event.customerId());
        // TODO: обработку ошибки при отправке уведомления
    }

}
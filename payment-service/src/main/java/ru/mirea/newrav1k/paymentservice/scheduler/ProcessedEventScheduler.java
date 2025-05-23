package ru.mirea.newrav1k.paymentservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mirea.newrav1k.paymentservice.service.IdempotencyService;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ProcessedEventScheduler {

    @Value("${payment-service.schedule.batch-size:10}")
    private Integer batchSize;

    private final IdempotencyService idempotencyService;

    @Scheduled(cron = "0 */5 * * * *")
    public void cleanProcessedEvents() {
        log.info("Cleaning processed events");
        this.idempotencyService.deleteBatchProcessed(this.batchSize);
    }

}
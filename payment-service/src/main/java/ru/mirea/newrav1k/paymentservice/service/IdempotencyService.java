package ru.mirea.newrav1k.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.newrav1k.paymentservice.model.entity.ProcessedEvent;
import ru.mirea.newrav1k.paymentservice.repository.ProcessedEventRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdempotencyService {

    private final ProcessedEventRepository processedEventRepository;

    public Boolean isProcessed(UUID eventId) {
        return this.processedEventRepository.existsById(eventId);
    }

    public void markProcessed(UUID eventId) {
        this.processedEventRepository.save(new ProcessedEvent(eventId));
    }

}
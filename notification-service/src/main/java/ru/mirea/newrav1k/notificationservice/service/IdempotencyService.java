package ru.mirea.newrav1k.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;

    public Boolean isProcessed(UUID eventId) {
        log.info("Checking if processed eventId is {}", eventId);
        return this.redisTemplate.hasKey(eventId.toString());
    }

    public void markProcessed(UUID eventId) {
        log.info("Marking processed event: {}", eventId);
        this.redisTemplate.opsForValue().set(eventId.toString(), Boolean.TRUE);
    }

}
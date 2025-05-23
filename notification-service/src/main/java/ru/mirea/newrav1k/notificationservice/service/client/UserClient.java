package ru.mirea.newrav1k.notificationservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.newrav1k.mirea.core.model.payload.UserResponse;

import java.util.UUID;

@Service
@FeignClient(
        name = "user-service",
        url = "${notification-service.services.user-service}"
)
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    UserResponse findByCustomerId(@PathVariable("userId") UUID customerId);

}
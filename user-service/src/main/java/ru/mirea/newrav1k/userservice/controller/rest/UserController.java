package ru.mirea.newrav1k.userservice.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mirea.newrav1k.userservice.model.dto.UserPayload;
import ru.mirea.newrav1k.userservice.service.UserService;
import ru.newrav1k.mirea.core.model.payload.UserResponse;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users/{userId}")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> loadUser(@PathVariable("userId") UUID userId) {
        log.info("Loading user with id {}", userId);
        UserResponse response = this.userService.findById(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponse> updateUser(@PathVariable("userId") UUID userId, @Valid UserPayload payload) {
        log.info("Updating user with id {}", userId);
        UserResponse user = this.userService.update(userId, payload);
        return ResponseEntity.ok(user);
    }

    @PatchMapping
    public ResponseEntity<UserResponse> patchUser(@PathVariable("userId") UUID userId, @RequestBody JsonNode jsonNode) {
        log.info("Patching user with id {}", userId);
        UserResponse user = this.userService.update(userId, jsonNode);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID userId) {
        log.info("Deleting user with id {}", userId);
        this.userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

}
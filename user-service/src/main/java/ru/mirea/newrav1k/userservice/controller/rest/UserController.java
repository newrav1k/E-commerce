package ru.mirea.newrav1k.userservice.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.mirea.newrav1k.userservice.nodel.dto.UserCreateRequest;
import ru.mirea.newrav1k.userservice.service.UserService;
import ru.newrav1k.mirea.core.model.payload.UserResponse;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> loadUser(@PathVariable("userId") UUID userId) {
        log.info("Loading user with id {}", userId);
        UserResponse response = this.userService.findById(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request,
                                                   UriComponentsBuilder uriBuilder) {
        log.info("Creating new user");
        UserResponse response = this.userService.save(request);
        return ResponseEntity.ok(response);
    }

}
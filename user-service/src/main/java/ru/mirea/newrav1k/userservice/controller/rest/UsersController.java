package ru.mirea.newrav1k.userservice.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.mirea.newrav1k.userservice.model.dto.UserCreateRequest;
import ru.mirea.newrav1k.userservice.service.UserService;
import ru.newrav1k.mirea.core.model.payload.UserResponse;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @GetMapping
    public PagedModel<UserResponse> loadAllUsers(@PageableDefault Pageable pageable) {
        log.info("Loading all users from page: {}", pageable);
        return new PagedModel<>(this.userService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request,
                                                   UriComponentsBuilder uriBuilder) {
        log.info("Creating user by request: {}", request);
        UserResponse user = this.userService.save(request);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/users/{userId}")
                        .build(user.userId()))
                .body(user);
    }

}
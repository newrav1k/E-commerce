package ru.mirea.newrav1k.userservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mirea.newrav1k.userservice.mapper.UserMapper;
import ru.mirea.newrav1k.userservice.model.dto.UserPayload;
import ru.mirea.newrav1k.userservice.model.entity.User;
import ru.mirea.newrav1k.userservice.model.dto.UserCreateRequest;
import ru.mirea.newrav1k.userservice.repository.UserRepository;
import ru.newrav1k.mirea.core.model.payload.UserResponse;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final ObjectMapper objectMapper;

    @Transactional
    public UserResponse save(UserCreateRequest request) {
        log.info("Saving user from request: {}", request);
        User user = new User();

        user.setId(request.id());
        user.setUsername(request.username());

        this.userRepository.save(user);
        return this.userMapper.toUserResponse(user);
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        log.info("Finding all users from page: {}", pageable);
        return this.userRepository.findAll(pageable)
                .map(this.userMapper::toUserResponse);
    }

    public UserResponse findById(UUID userId) {
        log.info("Finding user by id: {}", userId);
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return this.userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse update(UUID userId, UserPayload payload) {
        log.info("Updating user with id {} by payload: {}", userId, payload);
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setUsername(payload.username());
        return this.userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse update(UUID userId, JsonNode jsonNode) {
        log.info("Updating user with id {} by jsonNode: {}", userId, jsonNode);
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        try {
            this.objectMapper.readerForUpdating(user).readValue(jsonNode);

            return this.userMapper.toUserResponse(user);
        } catch (IOException exception) {
            log.error("Error reading user json node: {}", jsonNode);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @Transactional
    public void deleteById(UUID userId) {
        log.info("Deleting user by id: {}", userId);
        this.userRepository.deleteById(userId);
    }

}
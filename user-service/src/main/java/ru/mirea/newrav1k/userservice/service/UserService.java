package ru.mirea.newrav1k.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mirea.newrav1k.userservice.mapper.UserMapper;
import ru.mirea.newrav1k.userservice.model.entity.User;
import ru.mirea.newrav1k.userservice.nodel.dto.UserCreateRequest;
import ru.mirea.newrav1k.userservice.repository.UserRepository;
import ru.newrav1k.mirea.core.model.payload.UserResponse;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional
    public UserResponse save(UserCreateRequest request) {
        log.info("Saving user from request: {}", request);
        User user = new User();

        user.setId(request.id());
        user.setUsername(request.username());

        this.userRepository.save(user);
        return this.userMapper.toUserResponse(user);
    }

    public UserResponse findById(UUID userId) {
        log.info("Finding user by id: {}", userId);
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return this.userMapper.toUserResponse(user);
    }

}
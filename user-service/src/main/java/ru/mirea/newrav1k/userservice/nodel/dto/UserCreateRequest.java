package ru.mirea.newrav1k.userservice.nodel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserCreateRequest(
        @NotNull UUID id,
        @NotNull @Email String username
) {

}
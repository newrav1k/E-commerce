package ru.mirea.newrav1k.userservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserPayload(
        @NotNull @Email String username
) {

}
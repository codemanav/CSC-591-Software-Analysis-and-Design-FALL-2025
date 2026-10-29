package com.ecocycle.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String username,
        @Email String email
) {}

package com.hogar360.auth.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SetPasswordRequest(@NotBlank String newPassword, @NotBlank String confirmPassword) {
}
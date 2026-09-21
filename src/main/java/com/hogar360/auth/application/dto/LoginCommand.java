package com.hogar360.auth.application.dto;

public record LoginCommand(String email, String rawPassword) {
}

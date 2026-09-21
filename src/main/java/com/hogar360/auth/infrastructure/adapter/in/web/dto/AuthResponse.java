package com.hogar360.auth.infrastructure.adapter.in.web.dto;


public record AuthResponse(String token, String tokenType, String name, String email,
                            String role, boolean onboardingCompleted) {
    public static AuthResponse of(String token, String name, String email, String role, boolean onboardingCompleted) {
        return new AuthResponse(token, "Bearer", name, email, role, onboardingCompleted);
    }
}

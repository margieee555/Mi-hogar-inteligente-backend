package com.hogar360.auth.infrastructure.adapter.in.web.dto;

public record AuthResponse(String token, String tokenType, String name, String email,
                           String role, boolean onboardingCompleted, String authProvider) {

    /** Compatibilidad: sin proveedor se asume cuenta local. */
    public AuthResponse(String token, String tokenType, String name, String email,
                        String role, boolean onboardingCompleted) {
        this(token, tokenType, name, email, role, onboardingCompleted, "LOCAL");
    }

    public static AuthResponse of(String token, String name, String email, String role, boolean onboardingCompleted) {
        return of(token, name, email, role, onboardingCompleted, "LOCAL");
    }

    public static AuthResponse of(String token, String name, String email, String role,
                                  boolean onboardingCompleted, String authProvider) {
        return new AuthResponse(token, "Bearer", name, email, role, onboardingCompleted, authProvider);
    }
}
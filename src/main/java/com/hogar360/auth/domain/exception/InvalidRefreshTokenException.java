package com.hogar360.auth.domain.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("La sesión expiró. Inicia sesión de nuevo");
    }
}
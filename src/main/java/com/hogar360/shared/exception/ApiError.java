package com.hogar360.shared.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estructura estándar de error para todas las respuestas de la API.
 * El frontend (Angular) siempre puede esperar este mismo formato:
 * "code" es estable (sirve para traducir) y "message" ya viene en el idioma de la petición.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        List<String> details
) {
    public static ApiError of(int status, String error, String code, String message) {
        return new ApiError(LocalDateTime.now(), status, error, code, message, List.of());
    }

    public static ApiError of(int status, String error, String code, String message, List<String> details) {
        return new ApiError(LocalDateTime.now(), status, error, code, message, details);
    }
}
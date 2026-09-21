package com.hogar360.auth.application.dto;

/**
 * Comando de entrada al caso de uso de registro.
 * Es un objeto de aplicación, distinto del DTO web (RegisterRequest).
 */
public record RegisterUserCommand(String name, String email, String rawPassword) {
}

package com.hogar360.auth.domain.port.out;

/**
 * Puerto de salida para encriptar/verificar contraseñas.
 * El dominio no sabe si detrás hay BCrypt, Argon2, etc.
 */
public interface PasswordEncoderPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}

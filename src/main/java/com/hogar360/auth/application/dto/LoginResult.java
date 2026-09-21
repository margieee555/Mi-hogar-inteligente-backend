package com.hogar360.auth.application.dto;

import com.hogar360.auth.domain.model.User;

/**
 * Resultado del caso de uso de login: el token generado + el usuario autenticado.
 */
public record LoginResult(String token, User user) {
}

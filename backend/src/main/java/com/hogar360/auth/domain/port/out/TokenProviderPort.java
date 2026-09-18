package com.hogar360.auth.domain.port.out;

import com.hogar360.auth.domain.model.User;

/**
 * Puerto de salida para generar tokens de sesión (JWT u otro mecanismo).
 */
public interface TokenProviderPort {
    String generateToken(User user);
}

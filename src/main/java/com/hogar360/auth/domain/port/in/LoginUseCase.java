package com.hogar360.auth.domain.port.in;

import com.hogar360.auth.application.dto.LoginCommand;
import com.hogar360.auth.application.dto.LoginResult;

/**
 * Puerto de entrada: qué puede pedirle el mundo exterior al dominio de login.
 * Devuelve el token ya generado junto con los datos básicos del usuario autenticado.
 */
public interface LoginUseCase {
    LoginResult login(LoginCommand command);
}

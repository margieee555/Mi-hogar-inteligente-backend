package com.hogar360.auth.domain.port.in;

import com.hogar360.auth.application.dto.RegisterUserCommand;
import com.hogar360.auth.domain.model.User;

/**
 * Puerto de entrada: qué puede pedirle el mundo exterior al dominio de registro.
 */
public interface RegisterUserUseCase {
    User register(RegisterUserCommand command);
}

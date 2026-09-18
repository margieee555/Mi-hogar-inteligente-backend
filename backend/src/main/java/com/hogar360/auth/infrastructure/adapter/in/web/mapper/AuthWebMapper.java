package com.hogar360.auth.infrastructure.adapter.in.web.mapper;

import com.hogar360.auth.application.dto.LoginCommand;
import com.hogar360.auth.application.dto.RegisterUserCommand;
import com.hogar360.auth.domain.exception.UserAlreadyExistsException;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.LoginRequest;
import com.hogar360.auth.infrastructure.adapter.in.web.dto.RegisterRequest;
import org.springframework.stereotype.Component;

/**
 * Traduce entre los DTOs de la capa web y los comandos de la capa de aplicación.
 * Mantiene a los controllers "delgados".
 */
@Component
public class AuthWebMapper {

    public RegisterUserCommand toCommand(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        return new RegisterUserCommand(request.name(), request.email(), request.password());
    }

    public LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.email(), request.password());
    }
}

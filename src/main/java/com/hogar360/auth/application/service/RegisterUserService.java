package com.hogar360.auth.application.service;

import com.hogar360.auth.application.dto.RegisterUserCommand;
import com.hogar360.auth.domain.exception.UserAlreadyExistsException;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.in.RegisterUserUseCase;
import com.hogar360.auth.domain.port.out.PasswordEncoderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

/**
 * Implementación del caso de uso "registrar usuario".
 * Vive en application porque orquesta el dominio + los puertos de salida,
 * pero sigue sin saber nada de HTTP, JPA, etc.
 */
@Service
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public RegisterUserService(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public User register(RegisterUserCommand command) {
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        String encodedPassword = passwordEncoderPort.encode(command.rawPassword());
        User newUser = User.createNew(command.name(), command.email(), encodedPassword);

        return userRepositoryPort.save(newUser);
    }
}

package com.hogar360.auth.application.service;

import com.hogar360.auth.application.dto.LoginCommand;
import com.hogar360.auth.application.dto.LoginResult;
import com.hogar360.auth.domain.exception.InvalidCredentialsException;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.in.LoginUseCase;
import com.hogar360.auth.domain.port.out.PasswordEncoderPort;
import com.hogar360.auth.domain.port.out.TokenProviderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;

    public LoginService(UserRepositoryPort userRepositoryPort,
                         PasswordEncoderPort passwordEncoderPort,
                         TokenProviderPort tokenProviderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public LoginResult login(LoginCommand command) {
        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoderPort.matches(command.rawPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = tokenProviderPort.generateToken(user);
        return new LoginResult(token, user);
    }
}

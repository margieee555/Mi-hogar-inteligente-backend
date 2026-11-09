package com.hogar360.auth.application.service;

import com.hogar360.auth.application.dto.LoginResult;
import com.hogar360.auth.domain.model.GoogleIdentity;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.in.LoginWithGoogleUseCase;
import com.hogar360.auth.domain.port.out.GoogleIdentityPort;
import com.hogar360.auth.domain.port.out.PasswordEncoderPort;
import com.hogar360.auth.domain.port.out.TokenProviderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoginWithGoogleService implements LoginWithGoogleUseCase {

    private final GoogleIdentityPort googleIdentityPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;

    public LoginWithGoogleService(GoogleIdentityPort googleIdentityPort,
                                  UserRepositoryPort userRepositoryPort,
                                  PasswordEncoderPort passwordEncoderPort,
                                  TokenProviderPort tokenProviderPort) {
        this.googleIdentityPort = googleIdentityPort;
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public LoginResult loginWithGoogle(String idToken) {
        GoogleIdentity identity = googleIdentityPort.verify(idToken);

        User user = userRepositoryPort.findByEmail(identity.email())
                .map(existing -> existing.isGoogleAccount()
                        ? existing
                        // El correo se registró con contraseña sin verificarlo: se anula esa contraseña
                        : userRepositoryPort.save(existing.linkedToGoogle(unusablePasswordHash())))
                .orElseGet(() -> userRepositoryPort.save(
                        User.createFromGoogle(displayName(identity), identity.email(), unusablePasswordHash())));

        return new LoginResult(tokenProviderPort.generateToken(user), user);
    }

    /** Hash de un valor aleatorio que nadie conoce: la cuenta no admite login con contraseña. */
    private String unusablePasswordHash() {
        return passwordEncoderPort.encode(UUID.randomUUID().toString());
    }

    private String displayName(GoogleIdentity identity) {
        if (identity.name() != null && !identity.name().isBlank()) {
            return identity.name().trim();
        }
        return identity.email().substring(0, identity.email().indexOf('@'));
    }
}
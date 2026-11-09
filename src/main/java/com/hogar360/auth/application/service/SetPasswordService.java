package com.hogar360.auth.application.service;

import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.in.SetPasswordUseCase;
import com.hogar360.auth.domain.port.out.PasswordEncoderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import com.hogar360.shared.exception.DomainValidationException;
import org.springframework.stereotype.Service;

@Service
public class SetPasswordService implements SetPasswordUseCase {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 72;   // límite de bcrypt

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public SetPasswordService(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void setPassword(String email, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.length() < MIN_LENGTH) {
            throw new DomainValidationException("error.password.tooShort",
                    "La contraseña debe tener al menos " + MIN_LENGTH + " caracteres", MIN_LENGTH);
        }
        if (newPassword.length() > MAX_LENGTH) {
            throw new DomainValidationException("error.password.tooLong",
                    "La contraseña no puede superar " + MAX_LENGTH + " caracteres", MAX_LENGTH);
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new DomainValidationException("error.password.mismatch", "Las contraseñas no coinciden");
        }

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new DomainValidationException("error.user.notFound", "No se encontró el usuario"));

        if (!user.isGoogleAccount()) {
            throw new DomainValidationException("error.password.notGoogleAccount",
                    "Esta cuenta ya tiene contraseña. Solo las cuentas de Google pueden definirla aquí");
        }

        userRepositoryPort.save(user.withPassword(passwordEncoderPort.encode(newPassword)));
    }
}

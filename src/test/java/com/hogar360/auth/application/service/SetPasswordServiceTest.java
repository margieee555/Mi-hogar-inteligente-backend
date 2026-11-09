package com.hogar360.auth.application.service;

import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.out.PasswordEncoderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import com.hogar360.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SetPasswordServiceTest {

    private static final String EMAIL = "ana@gmail.com";

    private final UserRepositoryPort users = mock(UserRepositoryPort.class);
    private final PasswordEncoderPort encoder = mock(PasswordEncoderPort.class);
    private final SetPasswordService service = new SetPasswordService(users, encoder);

    private User googleUser() {
        User user = mock(User.class);
        when(user.isGoogleAccount()).thenReturn(true);
        return user;
    }

    @Test
    void guardaLaContrasenaEncriptadaEnUnaCuentaDeGoogle() {
        User google = googleUser();
        User updated = mock(User.class);
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(google));
        when(encoder.encode("secreta123")).thenReturn("hash");
        when(google.withPassword("hash")).thenReturn(updated);

        service.setPassword(EMAIL, "secreta123", "secreta123");

        verify(users).save(updated);
    }

    @Test
    void rechazaUnaContrasenaCorta() {
        assertThatThrownBy(() -> service.setPassword(EMAIL, "corta", "corta"))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.password.tooShort");

        verify(users, never()).save(any());
    }

    @Test
    void rechazaUnaContrasenaMuyLarga() {
        String larga = "x".repeat(73);

        assertThatThrownBy(() -> service.setPassword(EMAIL, larga, larga))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.password.tooLong");
    }

    @Test
    void rechazaSiLasContrasenasNoCoinciden() {
        assertThatThrownBy(() -> service.setPassword(EMAIL, "secreta123", "otra12345"))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.password.mismatch");

        verify(users, never()).save(any());
    }

    @Test
    void rechazaSiLaCuentaNoEsDeGoogle() {
        User local = mock(User.class);
        when(local.isGoogleAccount()).thenReturn(false);
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(local));

        assertThatThrownBy(() -> service.setPassword(EMAIL, "secreta123", "secreta123"))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.password.notGoogleAccount");

        verify(users, never()).save(any());
    }

    @Test
    void rechazaSiElUsuarioNoExiste() {
        when(users.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setPassword(EMAIL, "secreta123", "secreta123"))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.user.notFound");
    }
}
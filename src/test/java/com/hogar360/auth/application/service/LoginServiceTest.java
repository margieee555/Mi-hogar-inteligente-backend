package com.hogar360.auth.application.service;

import com.hogar360.auth.application.dto.LoginCommand;
import com.hogar360.auth.application.dto.LoginResult;
import com.hogar360.auth.domain.exception.InvalidCredentialsException;
import com.hogar360.auth.domain.model.Role;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.out.PasswordEncoderPort;
import com.hogar360.auth.domain.port.out.TokenProviderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    private static final String EMAIL = "juan@mail.com";
    private static final String RAW_PASSWORD = "1234";
    private static final String ENCODED_PASSWORD = "hash123";

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private PasswordEncoderPort passwordEncoderPort;
    @Mock
    private TokenProviderPort tokenProviderPort;

    @InjectMocks
    private LoginService loginService;

    private User buildUser() {
        return new User(1L, "Juan", EMAIL, ENCODED_PASSWORD, Role.USER, LocalDateTime.now());
    }

    @Test
    void login_conCredencialesValidas_retornaTokenYUsuario() {
        User user = buildUser();
        when(userRepositoryPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(tokenProviderPort.generateToken(user)).thenReturn("jwt-token");

        LoginResult result = loginService.login(new LoginCommand(EMAIL, RAW_PASSWORD));

        assertNotNull(result);
        assertEquals("jwt-token", result.token());
        assertSame(user, result.user());
        verify(tokenProviderPort).generateToken(user);
    }

    @Test
    void login_conEmailInexistente_lanzaInvalidCredentialsException() {
        when(userRepositoryPort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> loginService.login(new LoginCommand(EMAIL, RAW_PASSWORD)));

        assertEquals("Correo o contraseña incorrectos", ex.getMessage());
        verifyNoInteractions(passwordEncoderPort, tokenProviderPort);
    }

    @Test
    void login_conPasswordIncorrecta_lanzaInvalidCredentialsException() {
        User user = buildUser();
        when(userRepositoryPort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoderPort.matches("mala", ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> loginService.login(new LoginCommand(EMAIL, "mala")));

        verify(tokenProviderPort, never()).generateToken(any());
    }
}
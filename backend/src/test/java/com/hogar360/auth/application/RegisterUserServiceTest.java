package com.hogar360.auth.application;

import com.hogar360.auth.application.dto.RegisterUserCommand;
import com.hogar360.auth.application.service.RegisterUserService;
import com.hogar360.auth.domain.exception.UserAlreadyExistsException;
import com.hogar360.auth.domain.model.Role;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.out.PasswordEncoderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Test puro de aplicación: no levanta Spring, solo usa mocks de los puertos.
 * Así se prueban las reglas de negocio en milisegundos.
 */
class RegisterUserServiceTest {

    private final UserRepositoryPort userRepositoryPort = mock(UserRepositoryPort.class);
    private final PasswordEncoderPort passwordEncoderPort = mock(PasswordEncoderPort.class);
    private final RegisterUserService service = new RegisterUserService(userRepositoryPort, passwordEncoderPort);

    @Test
    void deberiaRegistrarUsuarioNuevoCorrectamente() {
        RegisterUserCommand command = new RegisterUserCommand("Ana García", "ana@ejemplo.com", "password123");

        when(userRepositoryPort.existsByEmail("ana@ejemplo.com")).thenReturn(false);
        when(passwordEncoderPort.encode("password123")).thenReturn("encoded-password");
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return new User(1L, u.getName(), u.getEmail(), u.getPassword(), Role.USER, LocalDateTime.now());
        });

        User result = service.register(command);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("ana@ejemplo.com");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void deberiaLanzarExcepcionSiElCorreoYaExiste() {
        RegisterUserCommand command = new RegisterUserCommand("Ana García", "ana@ejemplo.com", "password123");
        when(userRepositoryPort.existsByEmail("ana@ejemplo.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("ana@ejemplo.com");

        verify(userRepositoryPort, never()).save(any());
    }
}

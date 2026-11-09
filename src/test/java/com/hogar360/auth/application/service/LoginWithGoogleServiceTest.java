package com.hogar360.auth.application.service;

import com.hogar360.auth.application.dto.LoginResult;
import com.hogar360.auth.domain.exception.InvalidGoogleTokenException;
import com.hogar360.auth.domain.model.AuthProvider;
import com.hogar360.auth.domain.model.GoogleIdentity;
import com.hogar360.auth.domain.model.Role;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.out.GoogleIdentityPort;
import com.hogar360.auth.domain.port.out.PasswordEncoderPort;
import com.hogar360.auth.domain.port.out.TokenProviderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginWithGoogleServiceTest {

    @Mock private GoogleIdentityPort google;
    @Mock private UserRepositoryPort users;
    @Mock private PasswordEncoderPort encoder;
    @Mock private TokenProviderPort tokens;

    @InjectMocks private LoginWithGoogleService service;

    private static final String EMAIL = "ana@mail.com";

    @Test
    void conCorreoNuevo_creaLaCuentaDeGoogleConContraseñaInutilizable() {
        when(google.verify("tok")).thenReturn(new GoogleIdentity("sub", EMAIL, "Ana"));
        when(users.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn("hash-aleatorio");
        when(users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokens.generateToken(any(User.class))).thenReturn("jwt");

        LoginResult result = service.loginWithGoogle("tok");

        assertThat(result.token()).isEqualTo("jwt");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Ana");
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
        assertThat(captor.getValue().getAuthProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(captor.getValue().getPassword()).isEqualTo("hash-aleatorio");
    }

    @Test
    void sinNombreEnGoogle_usaLaParteLocalDelCorreo() {
        when(google.verify("tok")).thenReturn(new GoogleIdentity("sub", EMAIL, null));
        when(users.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn("hash");
        when(users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokens.generateToken(any(User.class))).thenReturn("jwt");

        LoginResult result = service.loginWithGoogle("tok");

        assertThat(result.user().getName()).isEqualTo("ana");
    }

    @Test
    void conCuentaDeGoogleExistente_iniciaSesionSinModificarla() {
        User existing = new User(5L, "Ana", EMAIL, "hash", Role.USER, LocalDateTime.now(), AuthProvider.GOOGLE);
        when(google.verify("tok")).thenReturn(new GoogleIdentity("sub", EMAIL, "Ana"));
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(tokens.generateToken(existing)).thenReturn("jwt");

        LoginResult result = service.loginWithGoogle("tok");

        assertThat(result.user()).isSameAs(existing);
        verify(users, never()).save(any());
        verifyNoInteractions(encoder);
    }

    @Test
    void conCuentaConContraseña_laVinculaYAnulaLaContraseñaAnterior() {
        User local = new User(5L, "Ana", EMAIL, "hash-del-atacante", Role.USER, LocalDateTime.now());
        when(google.verify("tok")).thenReturn(new GoogleIdentity("sub", EMAIL, "Ana"));
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(local));
        when(encoder.encode(anyString())).thenReturn("hash-inutilizable");
        when(users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokens.generateToken(any(User.class))).thenReturn("jwt");

        LoginResult result = service.loginWithGoogle("tok");

        assertThat(result.user().getId()).isEqualTo(5L);
        assertThat(result.user().getAuthProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(result.user().getPassword()).isEqualTo("hash-inutilizable");
    }

    @Test
    void conTokenInvalido_fallaSinTocarUsuarios() {
        when(google.verify("malo")).thenThrow(new InvalidGoogleTokenException());

        assertThatThrownBy(() -> service.loginWithGoogle("malo"))
                .isInstanceOf(InvalidGoogleTokenException.class);

        verifyNoInteractions(users, tokens);
    }
}

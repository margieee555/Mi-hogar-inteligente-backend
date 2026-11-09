package com.hogar360.auth.application.service;

import com.hogar360.auth.application.dto.RefreshResult;
import com.hogar360.auth.domain.exception.InvalidRefreshTokenException;
import com.hogar360.auth.domain.model.RefreshToken;
import com.hogar360.auth.domain.model.Role;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.out.RefreshTokenCodecPort;
import com.hogar360.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.hogar360.auth.domain.port.out.TokenProviderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock private RefreshTokenRepositoryPort refreshTokens;
    @Mock private RefreshTokenCodecPort codec;
    @Mock private UserRepositoryPort users;
    @Mock private TokenProviderPort tokenProvider;

    private SessionService service;
    private final User user = new User(1L, "Juan", "juan@mail.com", "hash", Role.USER, LocalDateTime.now());

    @BeforeEach
    void setUp() {
        service = new SessionService(refreshTokens, codec, users, tokenProvider, 7);
    }

    private RefreshToken storedToken(boolean revoked, LocalDateTime expiresAt) {
        return new RefreshToken(10L, 1L, "hash-old", "fam-1", expiresAt, revoked, LocalDateTime.now().minusHours(1));
    }

    @Test
    void openSession_guardaSoloElHashYDevuelveElTokenEnCrudo() {
        when(codec.generate()).thenReturn("raw-1");
        when(codec.hash("raw-1")).thenReturn("hash-1");

        String raw = service.openSession(user);

        assertThat(raw).isEqualTo("raw-1");
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokens).save(captor.capture());
        RefreshToken saved = captor.getValue();
        assertThat(saved.getTokenHash()).isEqualTo("hash-1");
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(saved.isRevoked()).isFalse();
        assertThat(saved.getFamilyId()).isNotBlank();
        assertThat(saved.getExpiresAt()).isAfter(LocalDateTime.now().plusDays(6));
    }

    @Test
    void refresh_conTokenValido_rotaYEmiteNuevoAccessToken() {
        when(codec.hash("raw-old")).thenReturn("hash-old");
        when(refreshTokens.findByTokenHash("hash-old"))
                .thenReturn(Optional.of(storedToken(false, LocalDateTime.now().plusDays(1))));
        when(users.findById(1L)).thenReturn(Optional.of(user));
        when(codec.generate()).thenReturn("raw-new");
        when(codec.hash("raw-new")).thenReturn("hash-new");
        when(tokenProvider.generateToken(user)).thenReturn("access-new");

        RefreshResult result = service.refresh("raw-old");

        assertThat(result.accessToken()).isEqualTo("access-new");
        assertThat(result.refreshToken()).isEqualTo("raw-new");
        assertThat(result.user()).isEqualTo(user);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokens, times(2)).save(captor.capture());
        List<RefreshToken> saved = captor.getAllValues();
        assertThat(saved.get(0).getId()).isEqualTo(10L);
        assertThat(saved.get(0).isRevoked()).isTrue();
        assertThat(saved.get(1).getTokenHash()).isEqualTo("hash-new");
        assertThat(saved.get(1).getFamilyId()).isEqualTo("fam-1");
        assertThat(saved.get(1).isRevoked()).isFalse();
    }

    @Test
    void refresh_conTokenYaUsado_revocaLaFamiliaYFalla() {
        when(codec.hash("raw-old")).thenReturn("hash-old");
        when(refreshTokens.findByTokenHash("hash-old"))
                .thenReturn(Optional.of(storedToken(true, LocalDateTime.now().plusDays(1))));

        assertThatThrownBy(() -> service.refresh("raw-old"))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verify(refreshTokens).revokeFamily("fam-1");
        verify(refreshTokens, never()).save(any());
    }

    @Test
    void refresh_conTokenExpirado_falla() {
        when(codec.hash("raw-old")).thenReturn("hash-old");
        when(refreshTokens.findByTokenHash("hash-old"))
                .thenReturn(Optional.of(storedToken(false, LocalDateTime.now().minusMinutes(1))));

        assertThatThrownBy(() -> service.refresh("raw-old"))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verify(refreshTokens, never()).save(any());
        verifyNoInteractions(users);
    }

    @Test
    void refresh_conTokenDesconocido_falla() {
        when(codec.hash("raw-x")).thenReturn("hash-x");
        when(refreshTokens.findByTokenHash("hash-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refresh("raw-x"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void refresh_sinToken_fallaSinTocarLaBase() {
        assertThatThrownBy(() -> service.refresh(null))
                .isInstanceOf(InvalidRefreshTokenException.class);
        assertThatThrownBy(() -> service.refresh("  "))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verifyNoInteractions(refreshTokens, codec);
    }

    @Test
    void closeSession_revocaLaFamiliaDelToken() {
        when(codec.hash("raw-old")).thenReturn("hash-old");
        when(refreshTokens.findByTokenHash("hash-old"))
                .thenReturn(Optional.of(storedToken(false, LocalDateTime.now().plusDays(1))));

        service.closeSession("raw-old");

        verify(refreshTokens).revokeFamily("fam-1");
    }

    @Test
    void closeSession_sinToken_noHaceNada() {
        service.closeSession(null);

        verifyNoInteractions(refreshTokens, codec);
    }
}
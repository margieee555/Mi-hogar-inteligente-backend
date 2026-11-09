package com.hogar360.auth.application.service;

import com.hogar360.auth.application.dto.RefreshResult;
import com.hogar360.auth.domain.exception.InvalidRefreshTokenException;
import com.hogar360.auth.domain.model.RefreshToken;
import com.hogar360.auth.domain.model.User;
import com.hogar360.auth.domain.port.in.SessionUseCase;
import com.hogar360.auth.domain.port.out.RefreshTokenCodecPort;
import com.hogar360.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.hogar360.auth.domain.port.out.TokenProviderPort;
import com.hogar360.auth.domain.port.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionService implements SessionUseCase {

    private final RefreshTokenRepositoryPort refreshTokens;
    private final RefreshTokenCodecPort codec;
    private final UserRepositoryPort users;
    private final TokenProviderPort tokenProvider;
    private final long refreshDays;

    public SessionService(RefreshTokenRepositoryPort refreshTokens,
                          RefreshTokenCodecPort codec,
                          UserRepositoryPort users,
                          TokenProviderPort tokenProvider,
                          @Value("${app.security.refresh-token-days:7}") long refreshDays) {
        this.refreshTokens = refreshTokens;
        this.codec = codec;
        this.users = users;
        this.tokenProvider = tokenProvider;
        this.refreshDays = refreshDays;
    }

    @Override
    @Transactional
    public String openSession(User user) {
        String raw = codec.generate();
        LocalDateTime now = LocalDateTime.now();
        refreshTokens.save(RefreshToken.issue(
                user.getId(), codec.hash(raw), UUID.randomUUID().toString(), now.plusDays(refreshDays), now));
        return raw;
    }

    /**
     * noRollbackFor: si se detecta reutilización de un token, la revocación de la familia
     * debe quedar guardada aunque después se lance la excepción.
     */
    @Override
    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public RefreshResult refresh(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }

        RefreshToken current = refreshTokens.findByTokenHash(codec.hash(rawRefreshToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        // Un token ya usado que vuelve a aparecer = posible robo: se cierra toda la cadena
        if (current.isRevoked()) {
            refreshTokens.revokeFamily(current.getFamilyId());
            throw new InvalidRefreshTokenException();
        }

        LocalDateTime now = LocalDateTime.now();
        if (current.isExpired(now)) {
            throw new InvalidRefreshTokenException();
        }

        User user = users.findById(current.getUserId())
                .orElseThrow(InvalidRefreshTokenException::new);

        // Rotación: el token usado se revoca y se entrega uno nuevo de la misma familia
        refreshTokens.save(current.revoke());
        String newRaw = codec.generate();
        refreshTokens.save(RefreshToken.issue(
                user.getId(), codec.hash(newRaw), current.getFamilyId(), now.plusDays(refreshDays), now));

        return new RefreshResult(tokenProvider.generateToken(user), newRaw, user);
    }

    @Override
    @Transactional
    public void closeSession(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }
        refreshTokens.findByTokenHash(codec.hash(rawRefreshToken))
                .ifPresent(token -> refreshTokens.revokeFamily(token.getFamilyId()));
    }
}
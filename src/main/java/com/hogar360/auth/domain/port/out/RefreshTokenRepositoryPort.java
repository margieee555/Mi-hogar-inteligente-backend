package com.hogar360.auth.domain.port.out;

import com.hogar360.auth.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    /** Revoca todos los tokens de la misma cadena de rotación. */
    void revokeFamily(String familyId);
}
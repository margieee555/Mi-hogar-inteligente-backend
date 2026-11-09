package com.hogar360.auth.domain.model;

import java.time.LocalDateTime;

/** Refresh token guardado: solo su hash, nunca el valor real. Inmutable. */
public class RefreshToken {

    private final Long id;
    private final Long userId;
    private final String tokenHash;
    private final String familyId;
    private final LocalDateTime expiresAt;
    private final boolean revoked;
    private final LocalDateTime createdAt;

    public RefreshToken(Long id, Long userId, String tokenHash, String familyId,
                        LocalDateTime expiresAt, boolean revoked, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.familyId = familyId;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static RefreshToken issue(Long userId, String tokenHash, String familyId,
                                     LocalDateTime expiresAt, LocalDateTime now) {
        return new RefreshToken(null, userId, tokenHash, familyId, expiresAt, false, now);
    }

    public boolean isExpired(LocalDateTime now) {
        return !expiresAt.isAfter(now);
    }

    public RefreshToken revoke() {
        return new RefreshToken(id, userId, tokenHash, familyId, expiresAt, true, createdAt);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public String getFamilyId() { return familyId; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
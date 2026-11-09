package com.hogar360.auth.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.auth.domain.model.RefreshToken;
import com.hogar360.auth.domain.port.out.RefreshTokenRepositoryPort;
import com.hogar360.auth.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity;
import com.hogar360.auth.infrastructure.adapter.out.persistence.repository.RefreshTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return toDomain(jpaRepository.save(toEntity(token)));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void revokeFamily(String familyId) {
        jpaRepository.revokeFamily(familyId);
    }

    private RefreshTokenEntity toEntity(RefreshToken t) {
        return new RefreshTokenEntity(t.getId(), t.getUserId(), t.getTokenHash(), t.getFamilyId(),
                t.getExpiresAt(), t.isRevoked(), t.getCreatedAt());
    }

    private RefreshToken toDomain(RefreshTokenEntity e) {
        return new RefreshToken(e.getId(), e.getUserId(), e.getTokenHash(), e.getFamilyId(),
                e.getExpiresAt(), e.isRevoked(), e.getCreatedAt());
    }
}
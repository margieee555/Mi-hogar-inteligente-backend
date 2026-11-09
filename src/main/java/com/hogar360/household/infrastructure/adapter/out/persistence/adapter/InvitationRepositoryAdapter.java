package com.hogar360.household.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.household.domain.model.Invitation;
import com.hogar360.household.domain.port.out.InvitationRepositoryPort;
import com.hogar360.household.infrastructure.adapter.out.persistence.entity.InvitationEntity;
import com.hogar360.household.infrastructure.adapter.out.persistence.repository.InvitationJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class InvitationRepositoryAdapter implements InvitationRepositoryPort {

    private final InvitationJpaRepository jpaRepository;

    public InvitationRepositoryAdapter(InvitationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Invitation save(Invitation invitation) {
        InvitationEntity entity = new InvitationEntity(
                invitation.getId(),
                invitation.getHouseholdId(),
                invitation.getEmail(),
                invitation.getStatus(),
                invitation.getCreatedAt() != null ? invitation.getCreatedAt() : LocalDateTime.now(),
                invitation.getExpiresAt()
        );
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Invitation> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Invitation> findPendingByHouseholdId(Long householdId) {
        return jpaRepository.findByHouseholdIdAndStatusOrderByIdDesc(householdId, Invitation.Status.PENDING)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<Invitation> findPendingByEmail(String email) {
        return jpaRepository.findByEmailIgnoreCaseAndStatusOrderByIdDesc(email, Invitation.Status.PENDING)
                .stream().map(this::toDomain).toList();
    }

    private Invitation toDomain(InvitationEntity e) {
        return new Invitation(e.getId(), e.getHouseholdId(), e.getEmail(), e.getStatus(),
                e.getCreatedAt(), e.getExpiresAt());
    }
}
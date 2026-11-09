package com.hogar360.household.infrastructure.adapter.out.persistence.repository;

import com.hogar360.household.domain.model.Invitation;
import com.hogar360.household.infrastructure.adapter.out.persistence.entity.InvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationJpaRepository extends JpaRepository<InvitationEntity, Long> {
    List<InvitationEntity> findByHouseholdIdAndStatusOrderByIdDesc(Long householdId, Invitation.Status status);
    List<InvitationEntity> findByEmailIgnoreCaseAndStatusOrderByIdDesc(String email, Invitation.Status status);
}
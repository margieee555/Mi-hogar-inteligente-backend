package com.hogar360.household.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.household.domain.model.HouseholdMember;
import com.hogar360.household.domain.port.out.HouseholdMemberRepositoryPort;
import com.hogar360.household.infrastructure.adapter.out.persistence.entity.HouseholdMemberEntity;
import com.hogar360.household.infrastructure.adapter.out.persistence.repository.HouseholdMemberJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class HouseholdMemberRepositoryAdapter implements HouseholdMemberRepositoryPort {

    private final HouseholdMemberJpaRepository jpaRepository;

    public HouseholdMemberRepositoryAdapter(HouseholdMemberJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public HouseholdMember save(HouseholdMember member) {
        HouseholdMemberEntity saved = jpaRepository.save(new HouseholdMemberEntity(
                member.id(), member.householdId(), member.userId(), member.role(),
                member.joinedAt() != null ? member.joinedAt() : LocalDateTime.now()));
        return toDomain(saved);
    }

    @Override
    public Optional<HouseholdMember> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Optional<HouseholdMember> findByHouseholdIdAndUserId(Long householdId, Long userId) {
        return jpaRepository.findByHouseholdIdAndUserId(householdId, userId).map(this::toDomain);
    }

    @Override
    public List<HouseholdMember> findByHouseholdId(Long householdId) {
        return jpaRepository.findByHouseholdIdOrderByIdAsc(householdId).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteByHouseholdIdAndUserId(Long householdId, Long userId) {
        jpaRepository.deleteByHouseholdIdAndUserId(householdId, userId);
    }

    private HouseholdMember toDomain(HouseholdMemberEntity e) {
        return new HouseholdMember(e.getId(), e.getHouseholdId(), e.getUserId(), e.getRole(), e.getJoinedAt());
    }
}   
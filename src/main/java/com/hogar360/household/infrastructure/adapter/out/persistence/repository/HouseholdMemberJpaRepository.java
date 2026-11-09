package com.hogar360.household.infrastructure.adapter.out.persistence.repository;

import com.hogar360.household.infrastructure.adapter.out.persistence.entity.HouseholdMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HouseholdMemberJpaRepository extends JpaRepository<HouseholdMemberEntity, Long> {
    Optional<HouseholdMemberEntity> findByUserId(Long userId);
    Optional<HouseholdMemberEntity> findByHouseholdIdAndUserId(Long householdId, Long userId);
    List<HouseholdMemberEntity> findByHouseholdIdOrderByIdAsc(Long householdId);
    void deleteByHouseholdIdAndUserId(Long householdId, Long userId);
}
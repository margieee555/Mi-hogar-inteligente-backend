package com.hogar360.household.infrastructure.adapter.out.persistence.repository;

import com.hogar360.household.infrastructure.adapter.out.persistence.entity.HouseholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HouseholdJpaRepository extends JpaRepository<HouseholdEntity, Long> {
    Optional<HouseholdEntity> findByOwnerUserId(Long ownerUserId);
    boolean existsByOwnerUserIdAndOnboardingCompletedTrue(Long ownerUserId);

    @Query("select h from HouseholdEntity h "
            + "where h.id in (select m.householdId from HouseholdMemberEntity m where m.userId = :userId)")
    Optional<HouseholdEntity> findByMemberUserId(@Param("userId") Long userId);

    @Query("select count(h) > 0 from HouseholdEntity h "
            + "where h.onboardingCompleted = true "
            + "and h.id in (select m.householdId from HouseholdMemberEntity m where m.userId = :userId)")
    boolean existsCompletedByMemberUserId(@Param("userId") Long userId);
}
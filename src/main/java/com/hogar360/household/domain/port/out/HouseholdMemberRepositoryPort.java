package com.hogar360.household.domain.port.out;

import com.hogar360.household.domain.model.HouseholdMember;

import java.util.List;
import java.util.Optional;

public interface HouseholdMemberRepositoryPort {
    HouseholdMember save(HouseholdMember member);

    Optional<HouseholdMember> findByUserId(Long userId);

    Optional<HouseholdMember> findByHouseholdIdAndUserId(Long householdId, Long userId);

    List<HouseholdMember> findByHouseholdId(Long householdId);

    void deleteByHouseholdIdAndUserId(Long householdId, Long userId);
}
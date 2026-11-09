package com.hogar360.household.domain.model;

import java.time.LocalDateTime;

public record HouseholdMember(Long id, Long householdId, Long userId, MemberRole role, LocalDateTime joinedAt) {
}
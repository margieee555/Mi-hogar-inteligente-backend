package com.hogar360.household.infrastructure.adapter.in.web.dto;

import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;

import java.util.List;

public record HouseholdResponse(
        Long id,
        String name,
        HomeType type,
        int residentCount,
        boolean onboardingCompleted,
        List<String> rooms
) {
    public static HouseholdResponse from(Household household) {
        return new HouseholdResponse(
                household.getId(),
                household.getName(),
                household.getType(),
                household.getResidentCount(),
                household.isOnboardingCompleted(),
                household.getRooms()
        );
    }
}

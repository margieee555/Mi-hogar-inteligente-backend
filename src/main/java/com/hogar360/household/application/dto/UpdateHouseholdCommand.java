package com.hogar360.household.application.dto;

import com.hogar360.household.domain.model.HomeType;

public record UpdateHouseholdCommand(Long householdId, String ownerEmail, String name, HomeType type,
                                     int residentCount) {
}
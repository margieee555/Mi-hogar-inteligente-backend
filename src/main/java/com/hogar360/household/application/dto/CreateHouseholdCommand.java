package com.hogar360.household.application.dto;

import com.hogar360.household.domain.model.HomeType;

public record CreateHouseholdCommand(String ownerEmail, String name, HomeType type, int residentCount) {
}
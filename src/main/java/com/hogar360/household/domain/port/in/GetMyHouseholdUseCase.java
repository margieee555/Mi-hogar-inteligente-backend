package com.hogar360.household.domain.port.in;

import com.hogar360.household.domain.model.Household;

public interface GetMyHouseholdUseCase {
    Household getByOwnerEmail(String ownerEmail);
}
package com.hogar360.household.domain.port.in;

import com.hogar360.household.application.dto.CreateHouseholdCommand;
import com.hogar360.household.domain.model.Household;

public interface CreateHouseholdUseCase {
    Household create(CreateHouseholdCommand command);
}
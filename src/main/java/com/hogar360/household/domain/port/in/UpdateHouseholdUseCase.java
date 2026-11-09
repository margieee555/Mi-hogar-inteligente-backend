package com.hogar360.household.domain.port.in;

import com.hogar360.household.application.dto.UpdateHouseholdCommand;
import com.hogar360.household.domain.model.Household;

public interface UpdateHouseholdUseCase {
    /** Solo el dueño. Cambia nombre, tipo y número de residentes. */
    Household update(UpdateHouseholdCommand command);
}
package com.hogar360.household.domain.port.in;

import com.hogar360.household.application.dto.AddRoomsCommand;
import com.hogar360.household.domain.model.Household;

public interface AddRoomsUseCase {
    Household addRooms(AddRoomsCommand command);
}

package com.hogar360.household.application.service;

import com.hogar360.household.application.dto.AddRoomsCommand;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.in.AddRoomsUseCase;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class AddRoomsService implements AddRoomsUseCase {

    private final HouseholdRepositoryPort householdRepositoryPort;
    private final HouseholdOwnershipChecker ownershipChecker;

    public AddRoomsService(HouseholdRepositoryPort householdRepositoryPort, HouseholdOwnershipChecker ownershipChecker) {
        this.householdRepositoryPort = householdRepositoryPort;
        this.ownershipChecker = ownershipChecker;
    }

    @Override
    public Household addRooms(AddRoomsCommand command) {
        Household household = ownershipChecker.requireOwned(command.householdId(), command.ownerEmail());
        return householdRepositoryPort.save(household.withRooms(command.rooms()));
    }
}

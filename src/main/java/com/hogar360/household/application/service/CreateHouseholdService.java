package com.hogar360.household.application.service;

import com.hogar360.household.application.dto.CreateHouseholdCommand;
import com.hogar360.household.domain.exception.AlreadyInHouseholdException;
import com.hogar360.household.domain.exception.HouseholdNotFoundException;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.in.CreateHouseholdUseCase;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateHouseholdService implements CreateHouseholdUseCase {

    private final HouseholdRepositoryPort householdRepositoryPort;
    private final UserLookupPort userLookupPort;

    public CreateHouseholdService(HouseholdRepositoryPort householdRepositoryPort, UserLookupPort userLookupPort) {
        this.householdRepositoryPort = householdRepositoryPort;
        this.userLookupPort = userLookupPort;
    }

    @Override
    public Household create(CreateHouseholdCommand command) {
        Long ownerUserId = userLookupPort.findUserIdByEmail(command.ownerEmail())
                .orElseThrow(() -> new HouseholdNotFoundException("No se encontró el usuario: " + command.ownerEmail()));

        Optional<Household> own = householdRepositoryPort.findByOwnerUserId(ownerUserId);

        // Quien ya es miembro del hogar de otra persona no puede crear uno propio
        if (own.isEmpty() && householdRepositoryPort.findByMemberUserId(ownerUserId).isPresent()) {
            throw new AlreadyInHouseholdException();
        }

        // Si el usuario ya empezó el onboarding, se actualiza el mismo hogar (no se crea otro)
        Household household = own
                .map(existing -> existing.withBasicInfo(command.name(), command.type(), command.residentCount()))
                .orElseGet(() -> Household.createNew(ownerUserId, command.name(), command.type(), command.residentCount()));

        return householdRepositoryPort.save(household);
    }
}
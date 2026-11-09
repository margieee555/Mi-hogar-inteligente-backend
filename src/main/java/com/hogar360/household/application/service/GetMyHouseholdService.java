package com.hogar360.household.application.service;

import com.hogar360.household.domain.exception.HouseholdNotFoundException;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.in.GetMyHouseholdUseCase;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import org.springframework.stereotype.Service;

@Service
public class GetMyHouseholdService implements GetMyHouseholdUseCase {

    private final HouseholdRepositoryPort householdRepositoryPort;
    private final UserLookupPort userLookupPort;

    public GetMyHouseholdService(HouseholdRepositoryPort householdRepositoryPort, UserLookupPort userLookupPort) {
        this.householdRepositoryPort = householdRepositoryPort;
        this.userLookupPort = userLookupPort;
    }

    /** El hogar propio o, si el usuario es un miembro invitado, el hogar al que pertenece. */
    @Override
    public Household getByOwnerEmail(String ownerEmail) {
        Long userId = userLookupPort.findUserIdByEmail(ownerEmail)
                .orElseThrow(() -> new HouseholdNotFoundException("No se encontró el usuario: " + ownerEmail));

        return householdRepositoryPort.findByOwnerUserId(userId)
                .or(() -> householdRepositoryPort.findByMemberUserId(userId))
                .orElseThrow(() -> new HouseholdNotFoundException("El usuario aún no ha configurado su hogar"));
    }
}
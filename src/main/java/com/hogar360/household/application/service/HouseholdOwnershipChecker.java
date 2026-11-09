package com.hogar360.household.application.service;

import com.hogar360.household.domain.exception.HouseholdAccessDeniedException;
import com.hogar360.household.domain.exception.HouseholdNotFoundException;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import org.springframework.stereotype.Component;

/**
 * Regla única de autorización: el hogar debe existir y pertenecer al usuario del token.
 * La reutilizan todos los casos de uso que modifican un hogar por id.
 */
@Component
public class HouseholdOwnershipChecker {

    private final HouseholdRepositoryPort householdRepositoryPort;
    private final UserLookupPort userLookupPort;

    public HouseholdOwnershipChecker(HouseholdRepositoryPort householdRepositoryPort, UserLookupPort userLookupPort) {
        this.householdRepositoryPort = householdRepositoryPort;
        this.userLookupPort = userLookupPort;
    }

    public Household requireOwned(Long householdId, String ownerEmail) {
        Long userId = userLookupPort.findUserIdByEmail(ownerEmail)
                .orElseThrow(() -> new HouseholdNotFoundException("No se encontró el usuario: " + ownerEmail));

        Household household = householdRepositoryPort.findById(householdId)
                .orElseThrow(() -> new HouseholdNotFoundException(householdId));

        if (!household.isOwnedBy(userId)) {
            throw new HouseholdAccessDeniedException();
        }
        return household;
    }
}

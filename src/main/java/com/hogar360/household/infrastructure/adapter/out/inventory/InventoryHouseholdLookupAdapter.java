package com.hogar360.household.infrastructure.adapter.out.inventory;

import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import com.hogar360.inventory.domain.port.out.InventoryHouseholdLookupPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryHouseholdLookupAdapter implements InventoryHouseholdLookupPort {

    private final UserLookupPort userLookupPort;
    private final HouseholdRepositoryPort householdRepositoryPort;

    public InventoryHouseholdLookupAdapter(UserLookupPort userLookupPort,
                                           HouseholdRepositoryPort householdRepositoryPort) {
        this.userLookupPort = userLookupPort;
        this.householdRepositoryPort = householdRepositoryPort;
    }

    @Override
    public Optional<Long> findHouseholdIdByOwnerEmail(String ownerEmail) {
        return userLookupPort.findUserIdByEmail(ownerEmail)
                .flatMap(householdRepositoryPort::findByMemberUserId)
                .map(Household::getId);
    }
}
package com.hogar360.household.infrastructure.adapter.out.finance;

import com.hogar360.finance.domain.port.out.HouseholdLookupPort;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HouseholdLookupAdapter implements HouseholdLookupPort {

    private final UserLookupPort userLookupPort;
    private final HouseholdRepositoryPort householdRepositoryPort;

    public HouseholdLookupAdapter(UserLookupPort userLookupPort, HouseholdRepositoryPort householdRepositoryPort) {
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
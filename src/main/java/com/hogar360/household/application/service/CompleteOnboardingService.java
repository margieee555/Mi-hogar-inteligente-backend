package com.hogar360.household.application.service;

import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.in.CompleteOnboardingUseCase;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CompleteOnboardingService implements CompleteOnboardingUseCase {

    private final HouseholdRepositoryPort householdRepositoryPort;
    private final HouseholdOwnershipChecker ownershipChecker;

    public CompleteOnboardingService(HouseholdRepositoryPort householdRepositoryPort,
                                     HouseholdOwnershipChecker ownershipChecker) {
        this.householdRepositoryPort = householdRepositoryPort;
        this.ownershipChecker = ownershipChecker;
    }

    @Override
    public Household complete(Long householdId, String ownerEmail) {
        Household household = ownershipChecker.requireOwned(householdId, ownerEmail);
        return householdRepositoryPort.save(household.markCompleted());
    }
}

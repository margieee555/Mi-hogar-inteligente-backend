package com.hogar360.household.application.service;

import com.hogar360.household.domain.port.in.HasHouseholdUseCase;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class HasHouseholdService implements HasHouseholdUseCase {

    private final HouseholdRepositoryPort householdRepositoryPort;

    public HasHouseholdService(HouseholdRepositoryPort householdRepositoryPort) {
        this.householdRepositoryPort = householdRepositoryPort;
    }

    /** Cuenta como "con hogar" tanto el dueño que terminó el onboarding como un miembro invitado. */
    @Override
    public boolean hasCompletedOnboarding(Long userId) {
        return householdRepositoryPort.existsCompletedByOwnerUserId(userId)
                || householdRepositoryPort.existsCompletedByMemberUserId(userId);
    }
}
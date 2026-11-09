package com.hogar360.household.application.service;

import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HasHouseholdServiceTest {

    @Mock
    private HouseholdRepositoryPort householdRepositoryPort;

    @InjectMocks
    private HasHouseholdService service;

    @Test
    void hasCompletedOnboarding_conOnboardingCompleto_retornaTrue() {
        when(householdRepositoryPort.existsCompletedByOwnerUserId(10L)).thenReturn(true);

        assertTrue(service.hasCompletedOnboarding(10L));
    }

    @Test
    void hasCompletedOnboarding_sinOnboardingCompleto_retornaFalse() {
        when(householdRepositoryPort.existsCompletedByOwnerUserId(10L)).thenReturn(false);

        assertFalse(service.hasCompletedOnboarding(10L));
    }
}
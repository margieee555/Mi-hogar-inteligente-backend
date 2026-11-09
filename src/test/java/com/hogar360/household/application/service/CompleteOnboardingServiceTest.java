package com.hogar360.household.application.service;

import com.hogar360.household.domain.exception.HouseholdAccessDeniedException;
import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteOnboardingServiceTest {

    private static final String EMAIL = "juan@mail.com";

    @Mock
    private HouseholdRepositoryPort householdRepositoryPort;
    @Mock
    private HouseholdOwnershipChecker ownershipChecker;

    @InjectMocks
    private CompleteOnboardingService service;

    @Test
    void complete_conPropietario_guardaElHogarCompletado() {
        Household household = new Household(5L, 10L, "Casa", HomeType.CASA, 2, false,
                List.of("Sala"), LocalDateTime.now());
        when(ownershipChecker.requireOwned(5L, EMAIL)).thenReturn(household);
        when(householdRepositoryPort.save(any(Household.class))).thenAnswer(inv -> inv.getArgument(0));

        Household result = service.complete(5L, EMAIL);

        ArgumentCaptor<Household> captor = ArgumentCaptor.forClass(Household.class);
        verify(householdRepositoryPort).save(captor.capture());
        assertTrue(captor.getValue().isOnboardingCompleted());
        assertTrue(result.isOnboardingCompleted());
        assertEquals(5L, result.getId());
    }

    @Test
    void complete_sinPermiso_propagaLaExcepcionYNoGuarda() {
        when(ownershipChecker.requireOwned(5L, EMAIL)).thenThrow(new HouseholdAccessDeniedException());

        assertThrows(HouseholdAccessDeniedException.class, () -> service.complete(5L, EMAIL));

        verify(householdRepositoryPort, never()).save(any());
    }
}
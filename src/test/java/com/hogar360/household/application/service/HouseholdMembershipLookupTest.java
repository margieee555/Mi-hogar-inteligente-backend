package com.hogar360.household.application.service;

import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HouseholdMembershipLookupTest {

    @Mock private HouseholdRepositoryPort householdRepository;
    @Mock private UserLookupPort userLookup;

    @Test
    void unMiembroInvitadoEncuentraElHogarAlQuePertenece() {
        Household hogar = new Household(10L, 1L, "Casa", HomeType.CASA, 3, true, List.of(), LocalDateTime.now());
        when(userLookup.findUserIdByEmail("luis@mail.com")).thenReturn(Optional.of(7L));
        when(householdRepository.findByOwnerUserId(7L)).thenReturn(Optional.empty());
        when(householdRepository.findByMemberUserId(7L)).thenReturn(Optional.of(hogar));

        Household encontrado = new GetMyHouseholdService(householdRepository, userLookup)
                .getByOwnerEmail("luis@mail.com");

        assertEquals(10L, encontrado.getId());
    }

    @Test
    void unMiembroCuentaComoUsuarioConHogarAunqueNoSeaDueno() {
        when(householdRepository.existsCompletedByOwnerUserId(7L)).thenReturn(false);
        when(householdRepository.existsCompletedByMemberUserId(7L)).thenReturn(true);

        assertTrue(new HasHouseholdService(householdRepository).hasCompletedOnboarding(7L));
    }
}
package com.hogar360.household.application.service;

import com.hogar360.household.domain.exception.HouseholdAccessDeniedException;
import com.hogar360.household.domain.exception.HouseholdNotFoundException;
import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseholdOwnershipCheckerTest {

    private static final String EMAIL = "juan@mail.com";

    @Mock
    private HouseholdRepositoryPort householdRepositoryPort;
    @Mock
    private UserLookupPort userLookupPort;

    @InjectMocks
    private HouseholdOwnershipChecker checker;

    private Household householdOwnedBy(Long ownerId) {
        return new Household(5L, ownerId, "Casa", HomeType.CASA, 2, false, List.of(), LocalDateTime.now());
    }

    @Test
    void requireOwned_conPropietario_retornaElHogar() {
        Household household = householdOwnedBy(10L);
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.of(10L));
        when(householdRepositoryPort.findById(5L)).thenReturn(Optional.of(household));

        assertSame(household, checker.requireOwned(5L, EMAIL));
    }

    @Test
    void requireOwned_conUsuarioInexistente_lanzaHouseholdNotFound() {
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(HouseholdNotFoundException.class, () -> checker.requireOwned(5L, EMAIL));

        verifyNoInteractions(householdRepositoryPort);
    }

    @Test
    void requireOwned_conHogarInexistente_lanzaHouseholdNotFoundConElId() {
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.of(10L));
        when(householdRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        HouseholdNotFoundException ex = assertThrows(HouseholdNotFoundException.class,
                () -> checker.requireOwned(99L, EMAIL));

        assertEquals("No se encontró un hogar con id: 99", ex.getMessage());
    }

    @Test
    void requireOwned_conHogarDeOtroUsuario_lanzaAccessDenied() {
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.of(10L));
        when(householdRepositoryPort.findById(5L)).thenReturn(Optional.of(householdOwnedBy(77L)));

        assertThrows(HouseholdAccessDeniedException.class, () -> checker.requireOwned(5L, EMAIL));
    }
}
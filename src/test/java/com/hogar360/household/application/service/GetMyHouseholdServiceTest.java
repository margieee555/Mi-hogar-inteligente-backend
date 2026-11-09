package com.hogar360.household.application.service;

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
class GetMyHouseholdServiceTest {

    private static final String EMAIL = "juan@mail.com";

    @Mock
    private HouseholdRepositoryPort householdRepositoryPort;
    @Mock
    private UserLookupPort userLookupPort;

    @InjectMocks
    private GetMyHouseholdService service;

    @Test
    void getByOwnerEmail_conHogarExistente_loRetorna() {
        Household household = new Household(5L, 10L, "Casa", HomeType.CASA, 2, false,
                List.of(), LocalDateTime.now());
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.of(10L));
        when(householdRepositoryPort.findByOwnerUserId(10L)).thenReturn(Optional.of(household));

        assertSame(household, service.getByOwnerEmail(EMAIL));
    }

    @Test
    void getByOwnerEmail_conUsuarioInexistente_lanzaHouseholdNotFound() {
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(HouseholdNotFoundException.class, () -> service.getByOwnerEmail(EMAIL));

        verifyNoInteractions(householdRepositoryPort);
    }

    @Test
    void getByOwnerEmail_sinHogarConfigurado_lanzaHouseholdNotFound() {
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.of(10L));
        when(householdRepositoryPort.findByOwnerUserId(10L)).thenReturn(Optional.empty());

        HouseholdNotFoundException ex = assertThrows(HouseholdNotFoundException.class,
                () -> service.getByOwnerEmail(EMAIL));

        assertEquals("El usuario aún no ha configurado su hogar", ex.getMessage());
    }
}
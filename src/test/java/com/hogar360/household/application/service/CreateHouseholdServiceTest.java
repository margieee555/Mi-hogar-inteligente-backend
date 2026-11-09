package com.hogar360.household.application.service;

import com.hogar360.household.application.dto.CreateHouseholdCommand;
import com.hogar360.household.domain.exception.HouseholdNotFoundException;
import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateHouseholdServiceTest {

    private static final String EMAIL = "juan@mail.com";

    @Mock
    private HouseholdRepositoryPort householdRepositoryPort;
    @Mock
    private UserLookupPort userLookupPort;

    @InjectMocks
    private CreateHouseholdService service;

    @Test
    void create_sinHogarPrevio_creaUnHogarNuevoYLoGuarda() {
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.of(10L));
        when(householdRepositoryPort.findByOwnerUserId(10L)).thenReturn(Optional.empty());
        when(householdRepositoryPort.save(any(Household.class))).thenAnswer(inv -> inv.getArgument(0));

        Household result = service.create(new CreateHouseholdCommand(EMAIL, "Casa Pérez", HomeType.CASA, 3));

        ArgumentCaptor<Household> captor = ArgumentCaptor.forClass(Household.class);
        verify(householdRepositoryPort).save(captor.capture());
        Household saved = captor.getValue();
        assertNull(saved.getId());
        assertEquals(10L, saved.getOwnerUserId());
        assertEquals("Casa Pérez", saved.getName());
        assertFalse(saved.isOnboardingCompleted());
        assertSame(saved, result);
    }

    @Test
    void create_conHogarExistente_actualizaElMismoHogarSinCrearOtro() {
        Household existing = new Household(5L, 10L, "Viejo", HomeType.CASA, 1, false,
                List.of("Sala"), LocalDateTime.now());
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.of(10L));
        when(householdRepositoryPort.findByOwnerUserId(10L)).thenReturn(Optional.of(existing));
        when(householdRepositoryPort.save(any(Household.class))).thenAnswer(inv -> inv.getArgument(0));

        Household result = service.create(new CreateHouseholdCommand(EMAIL, "Nuevo", HomeType.APARTAMENTO, 4));

        assertEquals(5L, result.getId());                 // mismo hogar
        assertEquals("Nuevo", result.getName());
        assertEquals(HomeType.APARTAMENTO, result.getType());
        assertEquals(4, result.getResidentCount());
        assertEquals(List.of("Sala"), result.getRooms()); // conserva habitaciones
    }

    @Test
    void create_conUsuarioInexistente_lanzaHouseholdNotFoundYNoGuarda() {
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(HouseholdNotFoundException.class,
                () -> service.create(new CreateHouseholdCommand(EMAIL, "Casa", HomeType.CASA, 2)));

        verifyNoInteractions(householdRepositoryPort);
    }

    @Test
    void create_conDatosInvalidos_lanzaIllegalArgumentYNoGuarda() {
        when(userLookupPort.findUserIdByEmail(EMAIL)).thenReturn(Optional.of(10L));
        when(householdRepositoryPort.findByOwnerUserId(10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.create(new CreateHouseholdCommand(EMAIL, " ", HomeType.CASA, 2)));

        verify(householdRepositoryPort, never()).save(any());
    }
}
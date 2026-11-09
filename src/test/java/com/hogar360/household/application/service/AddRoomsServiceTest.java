package com.hogar360.household.application.service;

import com.hogar360.household.application.dto.AddRoomsCommand;
import com.hogar360.household.domain.exception.HouseholdAccessDeniedException;
import com.hogar360.household.domain.model.HomeType;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddRoomsServiceTest {

    private static final String EMAIL = "juan@mail.com";

    @Mock
    private HouseholdRepositoryPort householdRepositoryPort;
    @Mock
    private HouseholdOwnershipChecker ownershipChecker;

    @InjectMocks
    private AddRoomsService service;

    private Household buildHousehold() {
        return new Household(5L, 10L, "Casa", HomeType.CASA, 2, false, List.of(), LocalDateTime.now());
    }

    @Test
    void addRooms_conPropietarioYHabitacionesValidas_guardaHabitacionesLimpias() {
        when(ownershipChecker.requireOwned(5L, EMAIL)).thenReturn(buildHousehold());
        when(householdRepositoryPort.save(any(Household.class))).thenAnswer(inv -> inv.getArgument(0));

        Household result = service.addRooms(new AddRoomsCommand(5L, EMAIL, List.of(" Sala ", "Cocina", "Sala")));

        assertEquals(List.of("Sala", "Cocina"), result.getRooms());
        verify(householdRepositoryPort).save(result);
    }

    @Test
    void addRooms_conListaVacia_lanzaIllegalArgumentYNoGuarda() {
        when(ownershipChecker.requireOwned(5L, EMAIL)).thenReturn(buildHousehold());

        assertThrows(IllegalArgumentException.class,
                () -> service.addRooms(new AddRoomsCommand(5L, EMAIL, List.of())));

        verify(householdRepositoryPort, never()).save(any());
    }

    @Test
    void addRooms_sinPermiso_propagaLaExcepcionYNoGuarda() {
        when(ownershipChecker.requireOwned(5L, EMAIL)).thenThrow(new HouseholdAccessDeniedException());

        assertThrows(HouseholdAccessDeniedException.class,
                () -> service.addRooms(new AddRoomsCommand(5L, EMAIL, List.of("Sala"))));

        verifyNoInteractions(householdRepositoryPort);
    }
}
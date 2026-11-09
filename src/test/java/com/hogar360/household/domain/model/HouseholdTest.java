package com.hogar360.household.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HouseholdTest {

    private Household buildHousehold() {
        return new Household(5L, 10L, "Casa Pérez", HomeType.CASA, 3, false,
                List.of("Sala"), LocalDateTime.now());
    }

    @Test
    void createNew_conDatosValidos_creaHogarSinCompletar() {
        Household h = Household.createNew(10L, "  Casa Pérez  ", HomeType.CASA, 3);

        assertNull(h.getId());
        assertEquals(10L, h.getOwnerUserId());
        assertEquals("Casa Pérez", h.getName());      // recorta espacios
        assertEquals(HomeType.CASA, h.getType());
        assertEquals(3, h.getResidentCount());
        assertFalse(h.isOnboardingCompleted());
        assertTrue(h.getRooms().isEmpty());
        assertNotNull(h.getCreatedAt());
    }

    @Test
    void createNew_conNombreVacio_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Household.createNew(10L, "   ", HomeType.CASA, 3));
    }

    @Test
    void createNew_conNombreNulo_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Household.createNew(10L, null, HomeType.CASA, 3));
    }

    @Test
    void createNew_conTipoNulo_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Household.createNew(10L, "Casa", null, 3));
    }

    @Test
    void createNew_sinResidentes_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Household.createNew(10L, "Casa", HomeType.CASA, 0));
    }

    @Test
    void withBasicInfo_actualizaDatosYConservaIdHabitacionesYEstado() {
        Household original = buildHousehold().markCompleted();

        Household updated = original.withBasicInfo("Apto Centro", HomeType.APARTAMENTO, 2);

        assertEquals(5L, updated.getId());
        assertEquals("Apto Centro", updated.getName());
        assertEquals(HomeType.APARTAMENTO, updated.getType());
        assertEquals(2, updated.getResidentCount());
        assertEquals(List.of("Sala"), updated.getRooms());
        assertTrue(updated.isOnboardingCompleted());
    }

    @Test
    void withRooms_recortaEspaciosQuitaVaciosNulosYDuplicados() {
        Household h = buildHousehold().withRooms(Arrays.asList(" Sala ", null, "  ", "Cocina", "Sala"));

        assertEquals(List.of("Sala", "Cocina"), h.getRooms());
    }

    @Test
    void withRooms_conListaNula_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> buildHousehold().withRooms(null));
    }

    @Test
    void withRooms_conListaVacia_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> buildHousehold().withRooms(List.of()));
    }

    @Test
    void withRooms_conSoloValoresEnBlanco_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> buildHousehold().withRooms(List.of(" ", "")));
    }

    @Test
    void markCompleted_marcaComoCompletadoSinModificarElOriginal() {
        Household original = buildHousehold();

        Household completed = original.markCompleted();

        assertTrue(completed.isOnboardingCompleted());
        assertFalse(original.isOnboardingCompleted()); // inmutable
    }

    @Test
    void isOwnedBy_conElPropietario_retornaTrue() {
        assertTrue(buildHousehold().isOwnedBy(10L));
    }

    @Test
    void isOwnedBy_conOtroUsuario_retornaFalse() {
        assertFalse(buildHousehold().isOwnedBy(99L));
    }

    @Test
    void isOwnedBy_conHogarSinPropietario_retornaFalse() {
        Household sinDueño = new Household(1L, null, "Casa", HomeType.CASA, 1, false,
                List.of(), LocalDateTime.now());

        assertFalse(sinDueño.isOwnedBy(10L));
    }
}
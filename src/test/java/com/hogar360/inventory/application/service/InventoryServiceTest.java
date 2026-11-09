package com.hogar360.inventory.application.service;

import com.hogar360.inventory.application.dto.CreateInventoryItemCommand;
import com.hogar360.inventory.application.dto.UpdateInventoryItemCommand;
import com.hogar360.inventory.domain.exception.InventoryHouseholdRequiredException;
import com.hogar360.inventory.domain.exception.InventoryItemNotFoundException;
import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.domain.model.InventoryItem;
import com.hogar360.inventory.domain.port.out.InventoryRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryServiceTest {

    private static final String EMAIL = "ana@x.com";

    private final InventoryRepositoryPort repository = mock(InventoryRepositoryPort.class);
    private final InventoryHouseholdResolver resolver = mock(InventoryHouseholdResolver.class);
    private final InventoryService service = new InventoryService(repository, resolver);

    private InventoryItem existing() {
        return new InventoryItem(7L, 5L, "Leche", InventoryCategory.FOOD, new BigDecimal("2.00"), "L",
                new BigDecimal("8.00"), null, LocalDateTime.now());
    }

    @Test
    void create_guardaElArticuloEnElHogarDelUsuario() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.save(any(InventoryItem.class))).thenAnswer(inv -> inv.getArgument(0));

        service.create(new CreateInventoryItemCommand(EMAIL, " Arroz ", InventoryCategory.FOOD,
                new BigDecimal("3"), "kg", new BigDecimal("10"), null));

        ArgumentCaptor<InventoryItem> captor = ArgumentCaptor.forClass(InventoryItem.class);
        verify(repository).save(captor.capture());
        InventoryItem saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getHouseholdId()).isEqualTo(5L);
        assertThat(saved.getName()).isEqualTo("Arroz");
    }

    @Test
    void create_noGuardaSiElUsuarioNoTieneHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenThrow(new InventoryHouseholdRequiredException());

        assertThatThrownBy(() -> service.create(new CreateInventoryItemCommand(EMAIL, "Arroz",
                InventoryCategory.FOOD, BigDecimal.ONE, "kg", BigDecimal.TEN, null)))
                .isInstanceOf(InventoryHouseholdRequiredException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void list_pideLosArticulosDelHogarConElFiltro() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByHousehold(5L, InventoryCategory.CLEANING)).thenReturn(List.of(existing()));

        assertThat(service.list(EMAIL, InventoryCategory.CLEANING)).hasSize(1);
    }

    @Test
    void update_conservaIdYHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));
        when(repository.save(any(InventoryItem.class))).thenAnswer(inv -> inv.getArgument(0));

        InventoryItem result = service.update(new UpdateInventoryItemCommand(7L, EMAIL, "Leche deslactosada",
                InventoryCategory.FOOD, new BigDecimal("4"), "L", new BigDecimal("8"), null));

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getHouseholdId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Leche deslactosada");
        assertThat(result.getQuantity()).isEqualByComparingTo("4.00");
    }

    @Test
    void update_lanzaNotFoundSiElArticuloNoEsDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(99L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(new UpdateInventoryItemCommand(99L, EMAIL, "X",
                InventoryCategory.FOOD, BigDecimal.ONE, "u.", BigDecimal.TEN, null)))
                .isInstanceOf(InventoryItemNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void changeQuantity_guardaLaNuevaCantidad() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));
        when(repository.save(any(InventoryItem.class))).thenAnswer(inv -> inv.getArgument(0));

        InventoryItem result = service.changeQuantity(7L, EMAIL, new BigDecimal("1"));

        assertThat(result.getQuantity()).isEqualByComparingTo("1.00");
        assertThat(result.getName()).isEqualTo("Leche");
    }

    @Test
    void delete_eliminaElArticuloDelHogar() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(7L, 5L)).thenReturn(Optional.of(existing()));

        service.delete(7L, EMAIL);

        verify(repository).deleteById(7L);
    }

    @Test
    void delete_lanzaNotFoundYNoBorraNada() {
        when(resolver.requireHouseholdId(EMAIL)).thenReturn(5L);
        when(repository.findByIdAndHouseholdId(99L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L, EMAIL))
                .isInstanceOf(InventoryItemNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }
}

package com.hogar360.inventory.infrastructure.adapter.out.shopping;

import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.domain.model.InventoryItem;
import com.hogar360.inventory.domain.port.out.InventoryRepositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryRestockAdapterTest {

    private final InventoryRepositoryPort repository = mock(InventoryRepositoryPort.class);
    private final InventoryRestockAdapter adapter = new InventoryRestockAdapter(repository);

    private InventoryItem leche() {
        return new InventoryItem(7L, 5L, "Leche", InventoryCategory.FOOD, new BigDecimal("2.00"), "L",
                new BigDecimal("8.00"), null, LocalDateTime.now());
    }

    @Test
    void restock_sumaAlArticuloExistente() {
        when(repository.findByHouseholdIdAndName(5L, "Leche")).thenReturn(Optional.of(leche()));
        when(repository.save(any(InventoryItem.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean repuesto = adapter.restock(5L, " Leche ", 3);

        ArgumentCaptor<InventoryItem> captor = ArgumentCaptor.forClass(InventoryItem.class);
        verify(repository).save(captor.capture());
        assertThat(repuesto).isTrue();
        assertThat(captor.getValue().getQuantity()).isEqualByComparingTo("5.00");
    }

    @Test
    void restock_sinArticulo_devuelveFalsoYNoGuarda() {
        when(repository.findByHouseholdIdAndName(5L, "Leche")).thenReturn(Optional.empty());

        boolean repuesto = adapter.restock(5L, "Leche", 3);

        assertThat(repuesto).isFalse();
        verify(repository, never()).save(any());
    }

    @Test
    void undoRestock_restaLoQueSeHabiaSumado() {
        when(repository.findByHouseholdIdAndName(5L, "Leche")).thenReturn(Optional.of(leche()));
        when(repository.save(any(InventoryItem.class))).thenAnswer(inv -> inv.getArgument(0));

        adapter.undoRestock(5L, "Leche", 1);

        ArgumentCaptor<InventoryItem> captor = ArgumentCaptor.forClass(InventoryItem.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getQuantity()).isEqualByComparingTo("1.00");
    }
}

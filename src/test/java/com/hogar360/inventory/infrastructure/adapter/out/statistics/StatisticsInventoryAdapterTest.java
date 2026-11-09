package com.hogar360.inventory.infrastructure.adapter.out.statistics;

import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.domain.model.InventoryItem;
import com.hogar360.inventory.domain.port.out.InventoryRepositoryPort;
import com.hogar360.statistics.domain.model.InventoryOverview;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticsInventoryAdapterTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 15);

    private static InventoryItem item(String name, String quantity, LocalDate expiration) {
        return new InventoryItem(null, 7L, name, InventoryCategory.FOOD, new BigDecimal(quantity), "u.",
                new BigDecimal("10"), expiration, LocalDateTime.now());
    }

    @Test
    void cuentaStockBajoPorVencerYVencidos() {
        InventoryRepositoryPort repository = mock(InventoryRepositoryPort.class);
        when(repository.findByHousehold(7L, null)).thenReturn(List.of(
                item("Poco", "1", null),                       // stock bajo (10%)
                item("PorVencer", "9", TODAY.plusDays(3)),     // vence en 3 días
                item("Vencido", "9", TODAY.minusDays(1)),      // ya venció
                item("Lejano", "9", TODAY.plusDays(30))));     // fuera de la ventana

        InventoryOverview overview = new StatisticsInventoryAdapter(repository).overview(7L, TODAY, 7);

        assertEquals(4, overview.total());
        assertEquals(1, overview.lowStock());
        assertEquals(1, overview.expiringSoon());
        assertEquals(1, overview.expired());
    }
}

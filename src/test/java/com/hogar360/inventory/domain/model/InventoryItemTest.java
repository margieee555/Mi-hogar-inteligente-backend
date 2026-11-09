package com.hogar360.inventory.domain.model;

import com.hogar360.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryItemTest {

    private static final LocalDate EXPIRES = LocalDate.of(2026, 11, 20);

    private InventoryItem nuevo(String cantidad, String ideal) {
        return InventoryItem.createNew(5L, "  Leche Entera ", InventoryCategory.FOOD,
                new BigDecimal(cantidad), " L ", new BigDecimal(ideal), EXPIRES);
    }

    @Test
    void createNew_normalizaTextosYCantidades() {
        InventoryItem item = nuevo("2", "8");

        assertThat(item.getId()).isNull();
        assertThat(item.getHouseholdId()).isEqualTo(5L);
        assertThat(item.getName()).isEqualTo("Leche Entera");
        assertThat(item.getUnit()).isEqualTo("L");
        assertThat(item.getQuantity()).isEqualByComparingTo("2.00");
        assertThat(item.getTargetQuantity()).isEqualByComparingTo("8.00");
        assertThat(item.getExpirationDate()).isEqualTo(EXPIRES);
        assertThat(item.getCreatedAt()).isNotNull();
    }

    @Test
    void createNew_rechazaNombreVacio() {
        assertThatThrownBy(() -> InventoryItem.createNew(5L, "  ", InventoryCategory.FOOD,
                BigDecimal.ONE, "u.", BigDecimal.TEN, null))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.inventory.name.required");
    }

    @Test
    void createNew_rechazaCantidadNegativa() {
        assertThatThrownBy(() -> InventoryItem.createNew(5L, "Arroz", InventoryCategory.FOOD,
                new BigDecimal("-1"), "kg", BigDecimal.TEN, null))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.inventory.quantity.invalid");
    }

    @Test
    void createNew_rechazaStockIdealEnCero() {
        assertThatThrownBy(() -> InventoryItem.createNew(5L, "Arroz", InventoryCategory.FOOD,
                BigDecimal.ONE, "kg", BigDecimal.ZERO, null))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.inventory.target.positive");
    }

    @Test
    void createNew_rechazaUnidadVacia() {
        assertThatThrownBy(() -> InventoryItem.createNew(5L, "Arroz", InventoryCategory.FOOD,
                BigDecimal.ONE, " ", BigDecimal.TEN, null))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.inventory.unit.required");
    }

    @Test
    void stockPercent_secalculaContraElIdealYTieneTopeDe100() {
        assertThat(nuevo("2", "8").stockPercent()).isEqualTo(25);
        assertThat(nuevo("20", "8").stockPercent()).isEqualTo(100);
        assertThat(nuevo("0", "8").stockPercent()).isZero();
    }

    @Test
    void isLowStock_esVerdaderoConVeinticincoPorCientoOMenos() {
        assertThat(nuevo("2", "8").isLowStock()).isTrue();
        assertThat(nuevo("3", "8").isLowStock()).isFalse();
    }

    @Test
    void withQuantity_cambiaSoloLaCantidadYValida() {
        InventoryItem item = nuevo("2", "8");

        InventoryItem ajustado = item.withQuantity(new BigDecimal("5"));

        assertThat(ajustado.getQuantity()).isEqualByComparingTo("5.00");
        assertThat(ajustado.getName()).isEqualTo("Leche Entera");
        assertThat(ajustado.getExpirationDate()).isEqualTo(EXPIRES);
        assertThatThrownBy(() -> item.withQuantity(new BigDecimal("-0.5")))
                .isInstanceOf(DomainValidationException.class)
                .hasFieldOrPropertyWithValue("code", "error.inventory.quantity.invalid");
    }
        @Test
    void restock_sumaLaCantidadComprada() {
        InventoryItem repuesto = nuevo("2", "8").restock(new BigDecimal("3"));

        assertThat(repuesto.getQuantity()).isEqualByComparingTo("5.00");
    }

    @Test
    void restock_noSuperaElMaximoPermitido() {
        InventoryItem repuesto = nuevo("99999999", "8").restock(new BigDecimal("5"));

        assertThat(repuesto.getQuantity()).isEqualByComparingTo("99999999.99");
    }

    @Test
    void consume_noBajaDeCero() {
        InventoryItem consumido = nuevo("2", "8").consume(new BigDecimal("5"));

        assertThat(consumido.getQuantity()).isEqualByComparingTo("0.00");
    }
}
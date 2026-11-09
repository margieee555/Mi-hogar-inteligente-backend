package com.hogar360.finance.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransactionTest {

    private static final LocalDate DATE = LocalDate.of(2026, 10, 1);

    private Transaction existing() {
        return new Transaction(7L, 5L, TransactionType.EXPENSE, TransactionCategory.FOOD,
                new BigDecimal("50000.00"), "Mercado", DATE, LocalDateTime.of(2026, 10, 1, 8, 0));
    }

    @Test
    void createNew_normalizaMontoYDescripcion() {
        Transaction t = Transaction.createNew(5L, TransactionType.EXPENSE, TransactionCategory.FOOD,
                new BigDecimal("12000.5"), "  Mercado semanal  ", DATE);

        assertThat(t.getId()).isNull();
        assertThat(t.getHouseholdId()).isEqualTo(5L);
        assertThat(t.getAmount()).isEqualByComparingTo("12000.50");
        assertThat(t.getAmount().scale()).isEqualTo(2);
        assertThat(t.getDescription()).isEqualTo("Mercado semanal");
        assertThat(t.getDate()).isEqualTo(DATE);
        assertThat(t.getCreatedAt()).isNotNull();
    }

    @Test
    void createNew_descripcionEnBlancoQuedaNula() {
        Transaction t = Transaction.createNew(5L, TransactionType.INCOME, TransactionCategory.SALARY,
                new BigDecimal("3000000"), "   ", DATE);

        assertThat(t.getDescription()).isNull();
    }

    @Test
    void createNew_aceptaDescripcionNula() {
        Transaction t = Transaction.createNew(5L, TransactionType.INCOME, TransactionCategory.SALARY,
                new BigDecimal("3000000"), null, DATE);

        assertThat(t.getDescription()).isNull();
    }

    @Test
    void createNew_rechazaTipoNulo() {
        assertThatThrownBy(() -> Transaction.createNew(5L, null, TransactionCategory.FOOD, BigDecimal.TEN, null, DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tipo");
    }

    @Test
    void createNew_rechazaCategoriaNula() {
        assertThatThrownBy(() -> Transaction.createNew(5L, TransactionType.EXPENSE, null, BigDecimal.TEN, null, DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("categoría");
    }

    @Test
    void createNew_rechazaCategoriaDeOtroTipo() {
        assertThatThrownBy(() -> Transaction.createNew(5L, TransactionType.EXPENSE, TransactionCategory.SALARY,
                BigDecimal.TEN, null, DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no corresponde");
    }

    @Test
    void createNew_rechazaMontoCero() {
        assertThatThrownBy(() -> Transaction.createNew(5L, TransactionType.EXPENSE, TransactionCategory.FOOD,
                BigDecimal.ZERO, null, DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monto");
    }

    @Test
    void createNew_rechazaMontoNegativo() {
        assertThatThrownBy(() -> Transaction.createNew(5L, TransactionType.EXPENSE, TransactionCategory.FOOD,
                new BigDecimal("-1"), null, DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monto");
    }

    @Test
    void createNew_rechazaMontoNulo() {
        assertThatThrownBy(() -> Transaction.createNew(5L, TransactionType.EXPENSE, TransactionCategory.FOOD,
                null, null, DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monto");
    }

    @Test
    void createNew_rechazaMontoSobreElMaximo() {
        assertThatThrownBy(() -> Transaction.createNew(5L, TransactionType.EXPENSE, TransactionCategory.FOOD,
                new BigDecimal("10000000000000"), null, DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("máximo");
    }

    @Test
    void createNew_rechazaFechaNula() {
        assertThatThrownBy(() -> Transaction.createNew(5L, TransactionType.EXPENSE, TransactionCategory.FOOD,
                BigDecimal.TEN, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fecha");
    }

    @Test
    void createNew_rechazaDescripcionMuyLarga() {
        assertThatThrownBy(() -> Transaction.createNew(5L, TransactionType.EXPENSE, TransactionCategory.FOOD,
                BigDecimal.TEN, "a".repeat(256), DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("descripción");
    }

    @Test
    void update_conservaIdHogarYFechaDeCreacion() {
        Transaction updated = existing().update(TransactionType.INCOME, TransactionCategory.EXTRA_INCOME,
                new BigDecimal("100"), "Venta", LocalDate.of(2026, 10, 5));

        assertThat(updated.getId()).isEqualTo(7L);
        assertThat(updated.getHouseholdId()).isEqualTo(5L);
        assertThat(updated.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 10, 1, 8, 0));
        assertThat(updated.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(updated.getCategory()).isEqualTo(TransactionCategory.EXTRA_INCOME);
        assertThat(updated.getAmount()).isEqualByComparingTo("100.00");
        assertThat(updated.getDescription()).isEqualTo("Venta");
        assertThat(updated.getDate()).isEqualTo(LocalDate.of(2026, 10, 5));
    }

    @Test
    void update_validaLasMismasReglas() {
        assertThatThrownBy(() -> existing().update(TransactionType.EXPENSE, TransactionCategory.SALARY,
                BigDecimal.TEN, null, DATE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no corresponde");
    }
}
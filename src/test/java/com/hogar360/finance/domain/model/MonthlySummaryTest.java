package com.hogar360.finance.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MonthlySummaryTest {

    private static final YearMonth PERIOD = YearMonth.of(2026, 10);

    private Transaction tx(TransactionType type, TransactionCategory category, String amount) {
        return new Transaction(null, 5L, type, category, new BigDecimal(amount), null,
                LocalDate.of(2026, 10, 1), LocalDateTime.now());
    }

    @Test
    void calculaTotalesYBalance() {
        MonthlySummary summary = MonthlySummary.of(PERIOD, List.of(
                tx(TransactionType.INCOME, TransactionCategory.SALARY, "3000000"),
                tx(TransactionType.EXPENSE, TransactionCategory.FOOD, "500000"),
                tx(TransactionType.EXPENSE, TransactionCategory.TRANSPORT, "200000"),
                tx(TransactionType.EXPENSE, TransactionCategory.FOOD, "100000")));

        assertThat(summary.period()).isEqualTo(PERIOD);
        assertThat(summary.totalIncome()).isEqualByComparingTo("3000000");
        assertThat(summary.totalExpense()).isEqualByComparingTo("800000");
        assertThat(summary.balance()).isEqualByComparingTo("2200000");
    }

    @Test
    void agrupaGastosPorCategoriaDeMayorAMenorSinIncluirIngresos() {
        MonthlySummary summary = MonthlySummary.of(PERIOD, List.of(
                tx(TransactionType.EXPENSE, TransactionCategory.TRANSPORT, "200000"),
                tx(TransactionType.EXPENSE, TransactionCategory.FOOD, "500000"),
                tx(TransactionType.EXPENSE, TransactionCategory.FOOD, "100000"),
                tx(TransactionType.INCOME, TransactionCategory.SALARY, "3000000")));

        assertThat(summary.expensesByCategory().keySet())
                .containsExactly(TransactionCategory.FOOD, TransactionCategory.TRANSPORT);
        assertThat(summary.expensesByCategory().get(TransactionCategory.FOOD)).isEqualByComparingTo("600000");
        assertThat(summary.expensesByCategory().get(TransactionCategory.TRANSPORT)).isEqualByComparingTo("200000");
    }

    @Test
    void balanceNegativoCuandoGastasMasDeLoQueIngresa() {
        MonthlySummary summary = MonthlySummary.of(PERIOD, List.of(
                tx(TransactionType.INCOME, TransactionCategory.SALARY, "100000"),
                tx(TransactionType.EXPENSE, TransactionCategory.RENT, "400000")));

        assertThat(summary.balance()).isEqualByComparingTo("-300000");
    }

    @Test
    void sinMovimientosTodoQuedaEnCero() {
        MonthlySummary summary = MonthlySummary.of(PERIOD, List.of());

        assertThat(summary.totalIncome()).isEqualByComparingTo("0");
        assertThat(summary.totalExpense()).isEqualByComparingTo("0");
        assertThat(summary.balance()).isEqualByComparingTo("0");
        assertThat(summary.expensesByCategory()).isEmpty();
    }
}
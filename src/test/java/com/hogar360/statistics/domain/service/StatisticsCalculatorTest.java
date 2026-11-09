package com.hogar360.statistics.domain.service;

import com.hogar360.statistics.domain.model.InventoryOverview;
import com.hogar360.statistics.domain.model.MovementSnapshot;
import com.hogar360.statistics.domain.model.ShoppingOverview;
import com.hogar360.statistics.domain.model.StatisticsPeriod;
import com.hogar360.statistics.domain.model.StatisticsReport;
import com.hogar360.statistics.domain.model.TaskCounts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatisticsCalculatorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 15);
    private static final TaskCounts TASKS = new TaskCounts(0, 0, 0);
    private static final InventoryOverview INVENTORY = new InventoryOverview(0, 0, 0, 0);
    private static final ShoppingOverview SHOPPING = new ShoppingOverview(0, 0);

    private static MovementSnapshot income(String category, String amount, LocalDate date) {
        return new MovementSnapshot(true, category, new BigDecimal(amount), date);
    }

    private static MovementSnapshot expense(String category, String amount, LocalDate date) {
        return new MovementSnapshot(false, category, new BigDecimal(amount), date);
    }

    private static StatisticsReport build(StatisticsPeriod period, List<MovementSnapshot> movements) {
        return StatisticsCalculator.build(period, TODAY, movements, TASKS, INVENTORY, SHOPPING);
    }

    private static List<MovementSnapshot> sample() {
        return List.of(
                income("SALARY", "3000", LocalDate.of(2026, 11, 1)),
                expense("FOOD", "800", LocalDate.of(2026, 11, 5)),
                expense("UTILITIES", "200", LocalDate.of(2026, 11, 10)),
                expense("FOOD", "500", LocalDate.of(2026, 10, 12)),
                expense("FOOD", "100", LocalDate.of(2026, 9, 20)));
    }

    @Test
    void mesCalculaTotalesCategoriaTopYComparacionConElMesAnterior() {
        StatisticsReport r = build(StatisticsPeriod.MONTH, sample());

        assertEquals(LocalDate.of(2026, 11, 1), r.from());
        assertEquals(LocalDate.of(2026, 11, 30), r.to());
        assertEquals(new BigDecimal("3000.00"), r.income());
        assertEquals(new BigDecimal("1000.00"), r.expense());
        assertEquals(new BigDecimal("2000.00"), r.savings());
        assertEquals(100, (int) r.expenseChangePercent()); // 1000 vs 500 de octubre

        assertEquals("FOOD", r.topExpense().category());
        assertEquals(new BigDecimal("800.00"), r.topExpense().amount());
        assertEquals(80, r.topExpense().percentOfExpenses());
        assertEquals(60, (int) r.topExpense().changePercent()); // 800 vs 500

        assertEquals(2, r.expensesByCategory().size());
        assertEquals("FOOD", r.expensesByCategory().get(0).category());
        assertEquals("UTILITIES", r.expensesByCategory().get(1).category());
        assertEquals(20, r.expensesByCategory().get(1).percent());

        assertEquals(3, r.monthly().size());
        assertEquals("2026-09", r.monthly().get(0).month().toString());
        assertEquals(new BigDecimal("100.00"), r.monthly().get(0).expense());
        assertEquals(new BigDecimal("500.00"), r.monthly().get(1).expense());
        assertEquals(new BigDecimal("2000.00"), r.monthly().get(2).savings());
    }

    @Test
    void trimestreSumaTresMesesYRedondeaElPorcentaje() {
        StatisticsReport r = build(StatisticsPeriod.QUARTER, sample());

        assertEquals(LocalDate.of(2026, 9, 1), r.from());
        assertEquals(new BigDecimal("1600.00"), r.expense());
        assertNull(r.expenseChangePercent()); // los 3 meses anteriores no tienen gastos
        assertEquals(new BigDecimal("1400.00"), r.topExpense().amount());
        assertEquals(88, r.topExpense().percentOfExpenses()); // 87.5 sube a 88
        assertNull(r.topExpense().changePercent());
    }

    @Test
    void anioUsaDoceMesesYComparaContraLosDoceAnteriores() {
        List<MovementSnapshot> movements = new java.util.ArrayList<>(sample());
        movements.add(expense("OTHER_EXPENSE", "999", LocalDate.of(2025, 11, 30))); // periodo anterior
        movements.add(expense("OTHER_EXPENSE", "50", LocalDate.of(2024, 5, 1)));    // fuera de todo

        StatisticsReport r = build(StatisticsPeriod.YEAR, movements);

        assertEquals(LocalDate.of(2025, 12, 1), r.from());
        assertEquals(new BigDecimal("1600.00"), r.expense()); // 2025-11-30 no entra
        assertEquals(60, (int) r.expenseChangePercent());     // 1600 vs 999
        assertEquals(12, r.monthly().size());
        assertEquals("2025-12", r.monthly().get(0).month().toString());
    }

    @Test
    void sinMovimientosTodoQuedaEnCeroYSinComparacion() {
        StatisticsReport r = build(StatisticsPeriod.MONTH, List.of());

        assertEquals(new BigDecimal("0.00"), r.income());
        assertEquals(new BigDecimal("0.00"), r.expense());
        assertEquals(new BigDecimal("0.00"), r.savings());
        assertNull(r.expenseChangePercent());
        assertNull(r.topExpense());
        assertTrue(r.expensesByCategory().isEmpty());
        assertEquals(3, r.monthly().size());
    }

    @Test
    void ventanasDeFechas() {
        assertEquals(LocalDate.of(2026, 11, 1), StatisticsCalculator.currentFrom(StatisticsPeriod.MONTH, TODAY));
        assertEquals(LocalDate.of(2026, 9, 1), StatisticsCalculator.currentFrom(StatisticsPeriod.QUARTER, TODAY));
        assertEquals(LocalDate.of(2026, 6, 1), StatisticsCalculator.previousFrom(StatisticsPeriod.QUARTER, TODAY));
        assertEquals(LocalDate.of(2026, 11, 30), StatisticsCalculator.currentTo(TODAY));
        // El mes necesita leer desde 2 meses atrás (la gráfica muestra 3 meses)
        assertEquals(LocalDate.of(2026, 9, 1), StatisticsCalculator.fetchFrom(StatisticsPeriod.MONTH, TODAY));
        assertEquals(LocalDate.of(2024, 12, 1), StatisticsCalculator.fetchFrom(StatisticsPeriod.YEAR, TODAY));
    }
}

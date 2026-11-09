package com.hogar360.statistics.domain.service;

import com.hogar360.statistics.domain.model.CategoryAmount;
import com.hogar360.statistics.domain.model.InventoryOverview;
import com.hogar360.statistics.domain.model.MonthSummary;
import com.hogar360.statistics.domain.model.MovementSnapshot;
import com.hogar360.statistics.domain.model.ShoppingOverview;
import com.hogar360.statistics.domain.model.StatisticsPeriod;
import com.hogar360.statistics.domain.model.StatisticsReport;
import com.hogar360.statistics.domain.model.TaskCounts;
import com.hogar360.statistics.domain.model.TopExpense;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Reglas de cálculo de Estadísticas: ventanas de fechas, totales, categorías y comparación. */
public final class StatisticsCalculator {

    private StatisticsCalculator() {
    }

    // ---- Ventanas de fechas ----
    public static LocalDate currentFrom(StatisticsPeriod period, LocalDate today) {
        return today.withDayOfMonth(1).minusMonths(period.getMonths() - 1L);
    }

    public static LocalDate currentTo(LocalDate today) {
        return today.withDayOfMonth(today.lengthOfMonth());
    }

    public static LocalDate previousFrom(StatisticsPeriod period, LocalDate today) {
        return currentFrom(period, today).minusMonths(period.getMonths());
    }

    public static LocalDate seriesFrom(StatisticsPeriod period, LocalDate today) {
        return today.withDayOfMonth(1).minusMonths(period.getSeriesMonths() - 1L);
    }

    /** Desde cuándo hay que leer movimientos para cubrir el periodo, el anterior y la gráfica. */
    public static LocalDate fetchFrom(StatisticsPeriod period, LocalDate today) {
        LocalDate previous = previousFrom(period, today);
        LocalDate series = seriesFrom(period, today);
        return previous.isBefore(series) ? previous : series;
    }

    // ---- Informe ----
    public static StatisticsReport build(StatisticsPeriod period, LocalDate today,
                                         List<MovementSnapshot> movements, TaskCounts tasks,
                                         InventoryOverview inventory, ShoppingOverview shopping) {
        LocalDate from = currentFrom(period, today);
        LocalDate to = currentTo(today);
        LocalDate previousFrom = previousFrom(period, today);
        LocalDate previousTo = from.minusDays(1);

        List<MovementSnapshot> current = within(movements, from, to);
        List<MovementSnapshot> previous = within(movements, previousFrom, previousTo);

        BigDecimal income = total(current, true);
        BigDecimal expense = total(current, false);
        BigDecimal previousExpense = total(previous, false);

        List<CategoryAmount> byCategory = expensesByCategory(current, expense);
        TopExpense top = byCategory.isEmpty() ? null : topExpense(byCategory.get(0), previous);

        return new StatisticsReport(period, from, to, income, expense, money(income.subtract(expense)),
                percentChange(expense, previousExpense), top, byCategory,
                monthly(period, today, movements), tasks, inventory, shopping);
    }

    private static TopExpense topExpense(CategoryAmount top, List<MovementSnapshot> previous) {
        BigDecimal previousAmount = previous.stream()
                .filter(m -> !m.income() && top.category().equals(m.category()))
                .map(MovementSnapshot::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TopExpense(top.category(), top.amount(), top.percent(),
                percentChange(top.amount(), previousAmount));
    }

    private static List<CategoryAmount> expensesByCategory(List<MovementSnapshot> current, BigDecimal totalExpense) {
        Map<String, BigDecimal> sums = new HashMap<>();
        for (MovementSnapshot m : current) {
            if (!m.income()) {
                sums.merge(m.category(), m.amount(), BigDecimal::add);
            }
        }
        return sums.entrySet().stream()
                .sorted((a, b) -> {
                    int byAmount = b.getValue().compareTo(a.getValue());
                    return byAmount != 0 ? byAmount : a.getKey().compareTo(b.getKey());
                })
                .map(e -> new CategoryAmount(e.getKey(), money(e.getValue()), percentOf(e.getValue(), totalExpense)))
                .toList();
    }

    private static List<MonthSummary> monthly(StatisticsPeriod period, LocalDate today,
                                              List<MovementSnapshot> movements) {
        YearMonth start = YearMonth.from(seriesFrom(period, today));
        List<MonthSummary> result = new ArrayList<>();
        for (int i = 0; i < period.getSeriesMonths(); i++) {
            YearMonth month = start.plusMonths(i);
            List<MovementSnapshot> ofMonth = movements.stream()
                    .filter(m -> m.date() != null && YearMonth.from(m.date()).equals(month))
                    .toList();
            BigDecimal income = total(ofMonth, true);
            BigDecimal expense = total(ofMonth, false);
            result.add(new MonthSummary(month, income, expense, money(income.subtract(expense))));
        }
        return result;
    }

    // ---- Utilidades ----
    private static List<MovementSnapshot> within(List<MovementSnapshot> movements, LocalDate from, LocalDate to) {
        return movements.stream()
                .filter(m -> m.date() != null && !m.date().isBefore(from) && !m.date().isAfter(to))
                .toList();
    }

    private static BigDecimal total(List<MovementSnapshot> movements, boolean income) {
        return money(movements.stream()
                .filter(m -> m.income() == income)
                .map(MovementSnapshot::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static int percentOf(BigDecimal part, BigDecimal whole) {
        if (whole.signum() == 0) {
            return 0;
        }
        return part.multiply(BigDecimal.valueOf(100)).divide(whole, 0, RoundingMode.HALF_UP).intValue();
    }

    /** Variación porcentual frente al periodo anterior; null si antes no hubo nada con qué comparar. */
    private static Integer percentChange(BigDecimal current, BigDecimal previous) {
        if (previous.signum() == 0) {
            return null;
        }
        return current.subtract(previous).multiply(BigDecimal.valueOf(100))
                .divide(previous, 0, RoundingMode.HALF_UP).intValue();
    }
}
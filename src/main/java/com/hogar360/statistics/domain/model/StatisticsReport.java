package com.hogar360.statistics.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record StatisticsReport(
        StatisticsPeriod period,
        LocalDate from,
        LocalDate to,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal savings,
        Integer expenseChangePercent,
        TopExpense topExpense,
        List<CategoryAmount> expensesByCategory,
        List<MonthSummary> monthly,
        TaskCounts tasks,
        InventoryOverview inventory,
        ShoppingOverview shopping) {
}
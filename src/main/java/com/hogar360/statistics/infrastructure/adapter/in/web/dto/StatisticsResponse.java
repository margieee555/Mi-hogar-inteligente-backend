package com.hogar360.statistics.infrastructure.adapter.in.web.dto;

import com.hogar360.statistics.domain.model.StatisticsReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record StatisticsResponse(
        String period,
        LocalDate from,
        LocalDate to,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal savings,
        Integer expenseChangePercent,
        TopExpenseResponse topExpense,
        List<CategoryResponse> expensesByCategory,
        List<MonthResponse> monthly,
        TasksResponse tasks,
        InventoryResponse inventory,
        ShoppingResponse shopping) {

    public record TopExpenseResponse(String category, BigDecimal amount, int percentOfExpenses, Integer changePercent) {
    }

    public record CategoryResponse(String category, BigDecimal amount, int percent) {
    }

    /** month con formato "2026-11". */
    public record MonthResponse(String month, BigDecimal income, BigDecimal expense, BigDecimal savings) {
    }

    public record TasksResponse(int pending, int inProgress, int completed, int total, int completionPercent) {
    }

    public record InventoryResponse(int total, int lowStock, int expiringSoon, int expired) {
    }

    public record ShoppingResponse(int pending, int bought) {
    }

    public static StatisticsResponse from(StatisticsReport report) {
        return new StatisticsResponse(
                report.period().name(),
                report.from(),
                report.to(),
                report.income(),
                report.expense(),
                report.savings(),
                report.expenseChangePercent(),
                report.topExpense() == null ? null : new TopExpenseResponse(
                        report.topExpense().category(), report.topExpense().amount(),
                        report.topExpense().percentOfExpenses(), report.topExpense().changePercent()),
                report.expensesByCategory().stream()
                        .map(c -> new CategoryResponse(c.category(), c.amount(), c.percent()))
                        .toList(),
                report.monthly().stream()
                        .map(m -> new MonthResponse(m.month().toString(), m.income(), m.expense(), m.savings()))
                        .toList(),
                new TasksResponse(report.tasks().pending(), report.tasks().inProgress(),
                        report.tasks().completed(), report.tasks().total(), report.tasks().completionPercent()),
                new InventoryResponse(report.inventory().total(), report.inventory().lowStock(),
                        report.inventory().expiringSoon(), report.inventory().expired()),
                new ShoppingResponse(report.shopping().pending(), report.shopping().bought()));
    }
}
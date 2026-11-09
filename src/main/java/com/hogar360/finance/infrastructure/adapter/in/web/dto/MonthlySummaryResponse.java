package com.hogar360.finance.infrastructure.adapter.in.web.dto;

import com.hogar360.finance.domain.model.MonthlySummary;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryResponse(
        int year,
        int month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<CategoryTotalResponse> expensesByCategory
) {
    public static MonthlySummaryResponse from(MonthlySummary summary) {
        List<CategoryTotalResponse> categories = summary.expensesByCategory().entrySet().stream()
                .map(entry -> new CategoryTotalResponse(entry.getKey(), entry.getKey().getLabel(), entry.getValue()))
                .toList();

        return new MonthlySummaryResponse(
                summary.period().getYear(),
                summary.period().getMonthValue(),
                summary.totalIncome(),
                summary.totalExpense(),
                summary.balance(),
                categories
        );
    }
}
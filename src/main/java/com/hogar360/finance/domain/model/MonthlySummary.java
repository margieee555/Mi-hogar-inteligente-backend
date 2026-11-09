package com.hogar360.finance.domain.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Resumen de un mes: totales, balance y gastos por categoría (de mayor a menor). */
public record MonthlySummary(
        YearMonth period,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        Map<TransactionCategory, BigDecimal> expensesByCategory
) {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);

    public static MonthlySummary of(YearMonth period, List<Transaction> transactions) {
        BigDecimal income = total(transactions, TransactionType.INCOME);
        BigDecimal expense = total(transactions, TransactionType.EXPENSE);

        Map<TransactionCategory, BigDecimal> byCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(Transaction::getCategory,
                        Collectors.reducing(ZERO, Transaction::getAmount, BigDecimal::add)));

        Map<TransactionCategory, BigDecimal> sorted = new LinkedHashMap<>();
        byCategory.entrySet().stream()
                .sorted(Map.Entry.<TransactionCategory, BigDecimal>comparingByValue().reversed())
                .forEach(entry -> sorted.put(entry.getKey(), entry.getValue()));

        return new MonthlySummary(period, income, expense, income.subtract(expense),
                Collections.unmodifiableMap(sorted));
    }

    private static BigDecimal total(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(ZERO, BigDecimal::add);
    }
}
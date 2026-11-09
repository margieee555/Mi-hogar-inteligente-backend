package com.hogar360.statistics.domain.model;

import java.math.BigDecimal;

/** changePercent es null cuando el periodo anterior no tuvo gasto en esa categoría. */
public record TopExpense(String category, BigDecimal amount, int percentOfExpenses, Integer changePercent) {
}
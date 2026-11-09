package com.hogar360.statistics.domain.model;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthSummary(YearMonth month, BigDecimal income, BigDecimal expense, BigDecimal savings) {
}
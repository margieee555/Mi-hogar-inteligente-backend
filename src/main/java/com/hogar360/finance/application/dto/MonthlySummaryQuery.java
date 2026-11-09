package com.hogar360.finance.application.dto;

import java.time.YearMonth;

public record MonthlySummaryQuery(String ownerEmail, YearMonth period) {
}
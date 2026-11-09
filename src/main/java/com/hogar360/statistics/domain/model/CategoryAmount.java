package com.hogar360.statistics.domain.model;

import java.math.BigDecimal;

public record CategoryAmount(String category, BigDecimal amount, int percent) {
}
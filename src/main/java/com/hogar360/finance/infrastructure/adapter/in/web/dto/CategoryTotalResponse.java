package com.hogar360.finance.infrastructure.adapter.in.web.dto;

import com.hogar360.finance.domain.model.TransactionCategory;

import java.math.BigDecimal;

public record CategoryTotalResponse(TransactionCategory category, String label, BigDecimal total) {
}
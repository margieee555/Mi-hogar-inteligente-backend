package com.hogar360.finance.application.dto;

import com.hogar360.finance.domain.model.TransactionCategory;
import com.hogar360.finance.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionCommand(
        String ownerEmail,
        TransactionType type,
        TransactionCategory category,
        BigDecimal amount,
        String description,
        LocalDate date
) {
}
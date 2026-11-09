package com.hogar360.finance.infrastructure.adapter.in.web.dto;

import com.hogar360.finance.domain.model.Transaction;
import com.hogar360.finance.domain.model.TransactionCategory;
import com.hogar360.finance.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        TransactionType type,
        TransactionCategory category,
        String categoryLabel,
        BigDecimal amount,
        String description,
        LocalDate date
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getCategory().getLabel(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate()
        );
    }
}
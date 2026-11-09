package com.hogar360.finance.application.dto;

import com.hogar360.finance.domain.model.TransactionType;

import java.time.YearMonth;

/** type puede ser null (= todos los tipos). */
public record ListTransactionsQuery(String ownerEmail, YearMonth period, TransactionType type) {
}
package com.hogar360.finance.infrastructure.adapter.in.web.dto;

import com.hogar360.finance.domain.model.TransactionCategory;
import com.hogar360.finance.domain.model.TransactionType;

/** Catálogo para el frontend: así no tiene que duplicar las categorías en el código Angular. */
public record CategoryResponse(TransactionCategory code, String label, TransactionType type) {

    public static CategoryResponse from(TransactionCategory category) {
        return new CategoryResponse(category, category.getLabel(), category.getType());
    }
}
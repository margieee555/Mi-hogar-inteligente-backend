package com.hogar360.inventory.application.dto;

import com.hogar360.inventory.domain.model.InventoryCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInventoryItemCommand(
        String ownerEmail,
        String name,
        InventoryCategory category,
        BigDecimal quantity,
        String unit,
        BigDecimal targetQuantity,
        LocalDate expirationDate
) {
}
package com.hogar360.inventory.infrastructure.adapter.in.web.dto;

import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.domain.model.InventoryItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InventoryItemResponse(
        Long id,
        String name,
        InventoryCategory category,
        BigDecimal quantity,
        String unit,
        BigDecimal targetQuantity,
        LocalDate expirationDate,
        int stockPercent,
        boolean lowStock
) {
    public static InventoryItemResponse from(InventoryItem item) {
        return new InventoryItemResponse(
                item.getId(),
                item.getName(),
                item.getCategory(),
                item.getQuantity(),
                item.getUnit(),
                item.getTargetQuantity(),
                item.getExpirationDate(),
                item.stockPercent(),
                item.isLowStock()
        );
    }
}
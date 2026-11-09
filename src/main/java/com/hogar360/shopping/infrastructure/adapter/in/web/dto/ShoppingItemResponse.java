package com.hogar360.shopping.infrastructure.adapter.in.web.dto;

import com.hogar360.shopping.domain.model.ShoppingCategory;
import com.hogar360.shopping.domain.model.ShoppingItem;

import java.time.LocalDateTime;

public record ShoppingItemResponse(
        Long id,
        String name,
        ShoppingCategory category,
        int quantity,
        boolean bought,
        LocalDateTime boughtAt,
        Integer restockedQuantity
) {
    public static ShoppingItemResponse from(ShoppingItem item) {
        return new ShoppingItemResponse(
                item.getId(),
                item.getName(),
                item.getCategory(),
                item.getQuantity(),
                item.isBought(),
                item.getBoughtAt(),
                item.getRestockedQuantity()
        );
    }
}
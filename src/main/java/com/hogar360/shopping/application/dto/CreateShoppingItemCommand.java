package com.hogar360.shopping.application.dto;

import com.hogar360.shopping.domain.model.ShoppingCategory;

public record CreateShoppingItemCommand(
        String ownerEmail,
        String name,
        ShoppingCategory category,
        int quantity
) {
}

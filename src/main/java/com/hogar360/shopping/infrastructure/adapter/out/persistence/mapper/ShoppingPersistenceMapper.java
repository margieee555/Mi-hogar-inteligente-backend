package com.hogar360.shopping.infrastructure.adapter.out.persistence.mapper;

import com.hogar360.shopping.domain.model.ShoppingItem;
import com.hogar360.shopping.infrastructure.adapter.out.persistence.entity.ShoppingItemEntity;
import org.springframework.stereotype.Component;

@Component
public class ShoppingPersistenceMapper {

    public ShoppingItemEntity toEntity(ShoppingItem item) {
        return new ShoppingItemEntity(
                item.getId(),
                item.getHouseholdId(),
                item.getName(),
                item.getCategory(),
                item.getQuantity(),
                item.isBought(),
                item.getBoughtAt(),
                item.getCreatedAt(),
                item.getRestockedQuantity()
        );
    }

    public ShoppingItem toDomain(ShoppingItemEntity entity) {
        return new ShoppingItem(
                entity.getId(),
                entity.getHouseholdId(),
                entity.getName(),
                entity.getCategory(),
                entity.getQuantity(),
                entity.isBought(),
                entity.getBoughtAt(),
                entity.getCreatedAt(),
                entity.getRestockedQuantity()
        );
    }
}

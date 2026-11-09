package com.hogar360.inventory.infrastructure.adapter.out.persistence.mapper;

import com.hogar360.inventory.domain.model.InventoryItem;
import com.hogar360.inventory.infrastructure.adapter.out.persistence.entity.InventoryItemEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryPersistenceMapper {

    public InventoryItemEntity toEntity(InventoryItem item) {
        return new InventoryItemEntity(
                item.getId(),
                item.getHouseholdId(),
                item.getName(),
                item.getCategory(),
                item.getQuantity(),
                item.getUnit(),
                item.getTargetQuantity(),
                item.getExpirationDate(),
                item.getCreatedAt()
        );
    }

    public InventoryItem toDomain(InventoryItemEntity entity) {
        return new InventoryItem(
                entity.getId(),
                entity.getHouseholdId(),
                entity.getName(),
                entity.getCategory(),
                entity.getQuantity(),
                entity.getUnit(),
                entity.getTargetQuantity(),
                entity.getExpirationDate(),
                entity.getCreatedAt()
        );
    }
}
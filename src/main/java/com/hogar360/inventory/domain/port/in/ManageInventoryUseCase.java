package com.hogar360.inventory.domain.port.in;

import com.hogar360.inventory.application.dto.CreateInventoryItemCommand;
import com.hogar360.inventory.application.dto.UpdateInventoryItemCommand;
import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.domain.model.InventoryItem;

import java.math.BigDecimal;
import java.util.List;

public interface ManageInventoryUseCase {
    InventoryItem create(CreateInventoryItemCommand command);

    List<InventoryItem> list(String ownerEmail, InventoryCategory category);

    InventoryItem update(UpdateInventoryItemCommand command);

    InventoryItem changeQuantity(Long itemId, String ownerEmail, BigDecimal quantity);

    void delete(Long itemId, String ownerEmail);
}
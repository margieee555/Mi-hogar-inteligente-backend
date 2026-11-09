package com.hogar360.inventory.application.service;

import com.hogar360.inventory.application.dto.CreateInventoryItemCommand;
import com.hogar360.inventory.application.dto.UpdateInventoryItemCommand;
import com.hogar360.inventory.domain.exception.InventoryItemNotFoundException;
import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.domain.model.InventoryItem;
import com.hogar360.inventory.domain.port.in.ManageInventoryUseCase;
import com.hogar360.inventory.domain.port.out.InventoryRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InventoryService implements ManageInventoryUseCase {

    private final InventoryRepositoryPort repository;
    private final InventoryHouseholdResolver householdResolver;

    public InventoryService(InventoryRepositoryPort repository, InventoryHouseholdResolver householdResolver) {
        this.repository = repository;
        this.householdResolver = householdResolver;
    }

    @Override
    public InventoryItem create(CreateInventoryItemCommand command) {
        Long householdId = householdResolver.requireHouseholdId(command.ownerEmail());

        InventoryItem item = InventoryItem.createNew(householdId, command.name(), command.category(),
                command.quantity(), command.unit(), command.targetQuantity(), command.expirationDate());

        return repository.save(item);
    }

    @Override
    public List<InventoryItem> list(String ownerEmail, InventoryCategory category) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);
        return repository.findByHousehold(householdId, category);
    }

    @Override
    public InventoryItem update(UpdateInventoryItemCommand command) {
        Long householdId = householdResolver.requireHouseholdId(command.ownerEmail());

        InventoryItem existing = repository.findByIdAndHouseholdId(command.itemId(), householdId)
                .orElseThrow(() -> new InventoryItemNotFoundException(command.itemId()));

        InventoryItem updated = existing.update(command.name(), command.category(), command.quantity(),
                command.unit(), command.targetQuantity(), command.expirationDate());

        return repository.save(updated);
    }

    @Override
    public InventoryItem changeQuantity(Long itemId, String ownerEmail, BigDecimal quantity) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);

        InventoryItem existing = repository.findByIdAndHouseholdId(itemId, householdId)
                .orElseThrow(() -> new InventoryItemNotFoundException(itemId));

        return repository.save(existing.withQuantity(quantity));
    }

    @Override
    public void delete(Long itemId, String ownerEmail) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);

        InventoryItem existing = repository.findByIdAndHouseholdId(itemId, householdId)
                .orElseThrow(() -> new InventoryItemNotFoundException(itemId));

        repository.deleteById(existing.getId());
    }
}
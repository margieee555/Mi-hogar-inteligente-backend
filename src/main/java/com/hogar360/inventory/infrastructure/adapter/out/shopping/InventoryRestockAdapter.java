package com.hogar360.inventory.infrastructure.adapter.out.shopping;

import com.hogar360.inventory.domain.port.out.InventoryRepositoryPort;
import com.hogar360.shopping.domain.port.out.InventoryRestockPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InventoryRestockAdapter implements InventoryRestockPort {

    private final InventoryRepositoryPort repository;

    public InventoryRestockAdapter(InventoryRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public boolean restock(Long householdId, String itemName, int amount) {
        return repository.findByHouseholdIdAndName(householdId, itemName.trim())
                .map(item -> {
                    repository.save(item.restock(BigDecimal.valueOf(amount)));
                    return true;
                })
                .orElse(false);
    }

    @Override
    public void undoRestock(Long householdId, String itemName, int amount) {
        repository.findByHouseholdIdAndName(householdId, itemName.trim())
                .ifPresent(item -> repository.save(item.consume(BigDecimal.valueOf(amount))));
    }
}
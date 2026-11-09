package com.hogar360.inventory.domain.port.out;

import com.hogar360.inventory.domain.model.InventoryCategory;
import com.hogar360.inventory.domain.model.InventoryItem;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    InventoryItem save(InventoryItem item);

    /** Busca por id DENTRO del hogar: un artículo de otro hogar se ve como inexistente. */
    Optional<InventoryItem> findByIdAndHouseholdId(Long id, Long householdId);

    /** Busca por nombre (sin distinguir mayúsculas) dentro del hogar. */
    Optional<InventoryItem> findByHouseholdIdAndName(Long householdId, String name);

    /** Artículos del hogar ordenados por nombre; category null = todas. */
    List<InventoryItem> findByHousehold(Long householdId, InventoryCategory category);

    void deleteById(Long id);
}

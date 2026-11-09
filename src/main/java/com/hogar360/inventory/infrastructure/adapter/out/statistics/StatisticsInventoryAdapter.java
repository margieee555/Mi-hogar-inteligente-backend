package com.hogar360.inventory.infrastructure.adapter.out.statistics;

import com.hogar360.inventory.domain.model.InventoryItem;
import com.hogar360.inventory.domain.port.out.InventoryRepositoryPort;
import com.hogar360.statistics.domain.model.InventoryOverview;
import com.hogar360.statistics.domain.port.out.StatisticsInventoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class StatisticsInventoryAdapter implements StatisticsInventoryPort {

    private final InventoryRepositoryPort inventoryRepository;

    public StatisticsInventoryAdapter(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public InventoryOverview overview(Long householdId, LocalDate today, int expiringDays) {
        List<InventoryItem> items = inventoryRepository.findByHousehold(householdId, null);
        LocalDate limit = today.plusDays(expiringDays);

        int lowStock = (int) items.stream().filter(InventoryItem::isLowStock).count();
        int expired = (int) items.stream()
                .filter(i -> i.getExpirationDate() != null && i.getExpirationDate().isBefore(today))
                .count();
        int expiringSoon = (int) items.stream()
                .filter(i -> i.getExpirationDate() != null
                        && !i.getExpirationDate().isBefore(today)
                        && !i.getExpirationDate().isAfter(limit))
                .count();

        return new InventoryOverview(items.size(), lowStock, expiringSoon, expired);
    }
}
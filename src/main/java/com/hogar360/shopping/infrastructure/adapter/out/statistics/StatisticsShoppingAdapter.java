package com.hogar360.shopping.infrastructure.adapter.out.statistics;

import com.hogar360.shopping.domain.model.ShoppingItem;
import com.hogar360.shopping.domain.port.out.ShoppingRepositoryPort;
import com.hogar360.statistics.domain.model.ShoppingOverview;
import com.hogar360.statistics.domain.port.out.StatisticsShoppingPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatisticsShoppingAdapter implements StatisticsShoppingPort {

    private final ShoppingRepositoryPort shoppingRepository;

    public StatisticsShoppingAdapter(ShoppingRepositoryPort shoppingRepository) {
        this.shoppingRepository = shoppingRepository;
    }

    @Override
    public ShoppingOverview overview(Long householdId) {
        List<ShoppingItem> items = shoppingRepository.findByHousehold(householdId);
        int bought = (int) items.stream().filter(ShoppingItem::isBought).count();
        return new ShoppingOverview(items.size() - bought, bought);
    }
}

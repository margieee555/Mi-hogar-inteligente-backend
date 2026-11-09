package com.hogar360.statistics.domain.port.out;

import com.hogar360.statistics.domain.model.ShoppingOverview;

public interface StatisticsShoppingPort {
    ShoppingOverview overview(Long householdId);
}
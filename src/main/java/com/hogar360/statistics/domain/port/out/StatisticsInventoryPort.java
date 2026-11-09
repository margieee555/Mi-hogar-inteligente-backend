package com.hogar360.statistics.domain.port.out;

import com.hogar360.statistics.domain.model.InventoryOverview;

import java.time.LocalDate;

public interface StatisticsInventoryPort {
    /** "Por vencer" = vence entre hoy y hoy + expiringDays; "vencidos" = ya pasó la fecha. */
    InventoryOverview overview(Long householdId, LocalDate today, int expiringDays);
}
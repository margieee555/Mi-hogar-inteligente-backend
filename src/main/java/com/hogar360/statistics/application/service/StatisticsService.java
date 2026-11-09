package com.hogar360.statistics.application.service;

import com.hogar360.statistics.domain.exception.StatisticsHouseholdRequiredException;
import com.hogar360.statistics.domain.model.MovementSnapshot;
import com.hogar360.statistics.domain.model.StatisticsPeriod;
import com.hogar360.statistics.domain.model.StatisticsReport;
import com.hogar360.statistics.domain.port.in.GetStatisticsUseCase;
import com.hogar360.statistics.domain.port.out.StatisticsFinancePort;
import com.hogar360.statistics.domain.port.out.StatisticsHouseholdLookupPort;
import com.hogar360.statistics.domain.port.out.StatisticsInventoryPort;
import com.hogar360.statistics.domain.port.out.StatisticsShoppingPort;
import com.hogar360.statistics.domain.port.out.StatisticsTasksPort;
import com.hogar360.statistics.domain.service.StatisticsCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatisticsService implements GetStatisticsUseCase {

    /** Un artículo cuenta como "por vencer" si caduca dentro de estos días. */
    public static final int EXPIRING_SOON_DAYS = 7;

    private final StatisticsHouseholdLookupPort householdLookupPort;
    private final StatisticsFinancePort financePort;
    private final StatisticsTasksPort tasksPort;
    private final StatisticsInventoryPort inventoryPort;
    private final StatisticsShoppingPort shoppingPort;

    public StatisticsService(StatisticsHouseholdLookupPort householdLookupPort,
                             StatisticsFinancePort financePort,
                             StatisticsTasksPort tasksPort,
                             StatisticsInventoryPort inventoryPort,
                             StatisticsShoppingPort shoppingPort) {
        this.householdLookupPort = householdLookupPort;
        this.financePort = financePort;
        this.tasksPort = tasksPort;
        this.inventoryPort = inventoryPort;
        this.shoppingPort = shoppingPort;
    }

    @Override
    public StatisticsReport get(String ownerEmail, StatisticsPeriod period) {
        return get(ownerEmail, period, LocalDate.now());
    }

    @Override
    public StatisticsReport get(String ownerEmail, StatisticsPeriod period, LocalDate today) {
        Long householdId = householdLookupPort.findHouseholdIdByOwnerEmail(ownerEmail)
                .orElseThrow(StatisticsHouseholdRequiredException::new);

        List<MovementSnapshot> movements = financePort.findMovements(householdId,
                StatisticsCalculator.fetchFrom(period, today), StatisticsCalculator.currentTo(today));

        return StatisticsCalculator.build(period, today, movements,
                tasksPort.countTasks(householdId),
                inventoryPort.overview(householdId, today, EXPIRING_SOON_DAYS),
                shoppingPort.overview(householdId));
    }
}
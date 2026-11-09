package com.hogar360.statistics.application.service;

import com.hogar360.statistics.domain.exception.StatisticsHouseholdRequiredException;
import com.hogar360.statistics.domain.model.InventoryOverview;
import com.hogar360.statistics.domain.model.MovementSnapshot;
import com.hogar360.statistics.domain.model.ShoppingOverview;
import com.hogar360.statistics.domain.model.StatisticsPeriod;
import com.hogar360.statistics.domain.model.StatisticsReport;
import com.hogar360.statistics.domain.model.TaskCounts;
import com.hogar360.statistics.domain.port.out.StatisticsFinancePort;
import com.hogar360.statistics.domain.port.out.StatisticsHouseholdLookupPort;
import com.hogar360.statistics.domain.port.out.StatisticsInventoryPort;
import com.hogar360.statistics.domain.port.out.StatisticsShoppingPort;
import com.hogar360.statistics.domain.port.out.StatisticsTasksPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 15);

    @Mock private StatisticsHouseholdLookupPort householdLookup;
    @Mock private StatisticsFinancePort finance;
    @Mock private StatisticsTasksPort tasks;
    @Mock private StatisticsInventoryPort inventory;
    @Mock private StatisticsShoppingPort shopping;

    private StatisticsService service;

    @BeforeEach
    void setUp() {
        service = new StatisticsService(householdLookup, finance, tasks, inventory, shopping);
    }

    private void stubHousehold() {
        when(householdLookup.findHouseholdIdByOwnerEmail("ana@mail.com")).thenReturn(Optional.of(7L));
    }

    @Test
    void sinHogarLanzaExcepcionYNoConsultaNada() {
        when(householdLookup.findHouseholdIdByOwnerEmail("ana@mail.com")).thenReturn(Optional.empty());

        assertThrows(StatisticsHouseholdRequiredException.class,
                () -> service.get("ana@mail.com", StatisticsPeriod.MONTH, TODAY));

        verifyNoInteractions(finance, tasks, inventory, shopping);
    }

    @Test
    void pideLosMovimientosDeLaVentanaCorrecta() {
        stubHousehold();
        when(finance.findMovements(7L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 11, 30))).thenReturn(List.of());
        when(tasks.countTasks(7L)).thenReturn(new TaskCounts(0, 0, 0));
        when(inventory.overview(7L, TODAY, StatisticsService.EXPIRING_SOON_DAYS))
                .thenReturn(new InventoryOverview(0, 0, 0, 0));
        when(shopping.overview(7L)).thenReturn(new ShoppingOverview(0, 0));

        StatisticsReport report = service.get("ana@mail.com", StatisticsPeriod.MONTH, TODAY);

        assertEquals(StatisticsPeriod.MONTH, report.period());
        verify(finance).findMovements(7L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 11, 30));
    }

    @Test
    void armaElInformeConLosDatosDeCadaModulo() {
        stubHousehold();
        when(finance.findMovements(7L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 11, 30)))
                .thenReturn(List.of(new MovementSnapshot(false, "FOOD", new BigDecimal("50"), TODAY)));
        when(tasks.countTasks(7L)).thenReturn(new TaskCounts(1, 2, 3));
        when(inventory.overview(7L, TODAY, StatisticsService.EXPIRING_SOON_DAYS))
                .thenReturn(new InventoryOverview(10, 2, 1, 0));
        when(shopping.overview(7L)).thenReturn(new ShoppingOverview(4, 5));

        StatisticsReport report = service.get("ana@mail.com", StatisticsPeriod.MONTH, TODAY);

        assertEquals(new BigDecimal("50.00"), report.expense());
        assertEquals(new TaskCounts(1, 2, 3), report.tasks());
        assertEquals(2, report.inventory().lowStock());
        assertEquals(5, report.shopping().bought());
    }
}
package com.hogar360.statistics.domain.port.out;

import com.hogar360.statistics.domain.model.MovementSnapshot;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsFinancePort {
    /** Movimientos del hogar entre dos fechas (ambas inclusive). */
    List<MovementSnapshot> findMovements(Long householdId, LocalDate from, LocalDate to);
}
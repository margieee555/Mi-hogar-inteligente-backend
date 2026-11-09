package com.hogar360.statistics.domain.port.in;

import com.hogar360.statistics.domain.model.StatisticsPeriod;
import com.hogar360.statistics.domain.model.StatisticsReport;

import java.time.LocalDate;

public interface GetStatisticsUseCase {

    StatisticsReport get(String ownerEmail, StatisticsPeriod period);

    /** Variante con fecha de referencia (para pruebas). */
    StatisticsReport get(String ownerEmail, StatisticsPeriod period, LocalDate today);
}
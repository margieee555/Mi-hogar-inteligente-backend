package com.hogar360.statistics.domain.model;

public enum StatisticsPeriod {
    MONTH(1, 3),
    QUARTER(3, 3),
    YEAR(12, 12);

    private final int months;
    private final int seriesMonths;

    StatisticsPeriod(int months, int seriesMonths) {
        this.months = months;
        this.seriesMonths = seriesMonths;
    }

    /** Meses que abarca el periodo. */
    public int getMonths() { return months; }

    /** Meses que muestra la gráfica comparativa. */
    public int getSeriesMonths() { return seriesMonths; }
}
package com.hogar360.statistics.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskCountsTest {

    @Test
    void calculaTotalYPorcentajeCompletado() {
        TaskCounts counts = new TaskCounts(2, 1, 1);

        assertEquals(4, counts.total());
        assertEquals(25, counts.completionPercent());
    }

    @Test
    void sinTareasElPorcentajeEsCero() {
        assertEquals(0, new TaskCounts(0, 0, 0).completionPercent());
    }
}
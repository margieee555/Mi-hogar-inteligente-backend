package com.hogar360.statistics.domain.model;

public record TaskCounts(int pending, int inProgress, int completed) {

    public int total() {
        return pending + inProgress + completed;
    }

    /** Porcentaje de tareas completadas (0 si no hay tareas). */
    public int completionPercent() {
        return total() == 0 ? 0 : Math.round(completed * 100f / total());
    }
}

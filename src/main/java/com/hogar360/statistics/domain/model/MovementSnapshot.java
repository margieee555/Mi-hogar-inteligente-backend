package com.hogar360.statistics.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Movimiento financiero tal como lo necesita Estadísticas (sin depender del módulo de finanzas). */
public record MovementSnapshot(boolean income, String category, BigDecimal amount, LocalDate date) {
}
package com.hogar360.household.domain.model;

/** Datos públicos de un usuario que el módulo household puede mostrar. */
public record UserSummary(Long id, String name, String email) {
}
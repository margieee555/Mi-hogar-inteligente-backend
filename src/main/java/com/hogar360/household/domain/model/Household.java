package com.hogar360.household.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.hogar360.shared.exception.DomainValidationException;

/**
 * Entidad de dominio pura (sin JPA ni Spring). Inmutable: cada cambio devuelve una copia nueva.
 */
public class Household {

    private final Long id;
    private final Long ownerUserId;
    private final String name;
    private final HomeType type;
    private final int residentCount;
    private final boolean onboardingCompleted;
    private final List<String> rooms;
    private final LocalDateTime createdAt;

    public Household(Long id, Long ownerUserId, String name, HomeType type, int residentCount,
                     boolean onboardingCompleted, List<String> rooms, LocalDateTime createdAt) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.type = type;
        this.residentCount = residentCount;
        this.onboardingCompleted = onboardingCompleted;
        this.rooms = List.copyOf(rooms);
        this.createdAt = createdAt;
    }

    /** Hogar nuevo (paso 1): sin id, sin habitaciones, onboarding sin completar. */
    public static Household createNew(Long ownerUserId, String name, HomeType type, int residentCount) {
        validateBasicInfo(name, type, residentCount);
        return new Household(null, ownerUserId, name.trim(), type, residentCount, false, List.of(), LocalDateTime.now());
    }

    /** Actualiza los datos del paso 1 conservando id, habitaciones y estado. */
    public Household withBasicInfo(String newName, HomeType newType, int newResidentCount) {
        validateBasicInfo(newName, newType, newResidentCount);
        return new Household(id, ownerUserId, newName.trim(), newType, newResidentCount,
                onboardingCompleted, rooms, createdAt);
    }

    /** Reemplaza las habitaciones (paso 2): recorta espacios, quita vacíos y duplicados. */
    public Household withRooms(List<String> newRooms) {
        if (newRooms == null) {
            throw new DomainValidationException("error.household.rooms.required", "Debes seleccionar al menos un espacio");
        }
        List<String> cleaned = newRooms.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(room -> !room.isBlank())
                .distinct()
                .toList();

        if (cleaned.isEmpty()) {
            throw new DomainValidationException("error.household.rooms.required", "Debes seleccionar al menos un espacio");
        }
        return new Household(id, ownerUserId, name, type, residentCount, onboardingCompleted, cleaned, createdAt);
    }

    /** Marca el onboarding como completado (paso 3). */
    public Household markCompleted() {
        return new Household(id, ownerUserId, name, type, residentCount, true, rooms, createdAt);
    }

    public boolean isOwnedBy(Long userId) {
        return ownerUserId != null && ownerUserId.equals(userId);
    }

    private static void validateBasicInfo(String name, HomeType type, int residentCount) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("error.household.name.required", "El nombre del hogar es obligatorio");
        }
        if (type == null) {
            throw new DomainValidationException("error.household.type.required", "El tipo de vivienda es obligatorio");
        }
        if (residentCount < 1) {
            throw new DomainValidationException("error.household.residentCount.invalid", "Debe haber al menos 1 residente");
        }
    }

    public Long getId() { return id; }
    public Long getOwnerUserId() { return ownerUserId; }
    public String getName() { return name; }
    public HomeType getType() { return type; }
    public int getResidentCount() { return residentCount; }
    public boolean isOnboardingCompleted() { return onboardingCompleted; }
    public List<String> getRooms() { return rooms; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
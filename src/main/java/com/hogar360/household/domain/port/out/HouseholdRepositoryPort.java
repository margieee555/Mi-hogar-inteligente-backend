package com.hogar360.household.domain.port.out;

import com.hogar360.household.domain.model.Household;

import java.util.Optional;

public interface HouseholdRepositoryPort {
    Household save(Household household);
    Optional<Household> findById(Long id);
    Optional<Household> findByOwnerUserId(Long ownerUserId);
    boolean existsCompletedByOwnerUserId(Long ownerUserId);

    /** Hogar al que pertenece el usuario, sea dueño o miembro. */
    Optional<Household> findByMemberUserId(Long userId);

    /** El usuario pertenece (como dueño o miembro) a un hogar con onboarding completado. */
    boolean existsCompletedByMemberUserId(Long userId);

    /** Elimina el hogar (sus habitaciones, miembros e invitaciones caen en cascada). */
    void deleteById(Long id);
}
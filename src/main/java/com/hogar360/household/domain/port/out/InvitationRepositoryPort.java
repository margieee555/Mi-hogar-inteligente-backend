package com.hogar360.household.domain.port.out;

import com.hogar360.household.domain.model.Invitation;

import java.util.List;
import java.util.Optional;

public interface InvitationRepositoryPort {
    Invitation save(Invitation invitation);

    Optional<Invitation> findById(Long id);

    /** Invitaciones en estado PENDING del hogar (pueden incluir vencidas: las filtra el servicio). */
    List<Invitation> findPendingByHouseholdId(Long householdId);

    /** Invitaciones en estado PENDING dirigidas a un correo (sin distinguir mayúsculas). */
    List<Invitation> findPendingByEmail(String email);
}
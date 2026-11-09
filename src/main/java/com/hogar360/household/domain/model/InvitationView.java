package com.hogar360.household.domain.model;

import java.time.LocalDateTime;

/** ownerName puede venir vacío en el listado que ve el dueño (ya sabe quién es). */
public record InvitationView(Long id, Long householdId, String householdName, String ownerName,
                             String email, LocalDateTime createdAt, LocalDateTime expiresAt) {
}
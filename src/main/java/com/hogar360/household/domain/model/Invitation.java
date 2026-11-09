package com.hogar360.household.domain.model;

import com.hogar360.household.domain.exception.InvitationNotPendingException;
import com.hogar360.shared.exception.DomainValidationException;

import java.time.LocalDateTime;

public class Invitation {

    public static final int VALID_DAYS = 7;

    public enum Status { PENDING, ACCEPTED, DECLINED, CANCELLED }

    private final Long id;
    private final Long householdId;
    private final String email;
    private final Status status;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    public Invitation(Long id, Long householdId, String email, Status status,
                      LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.householdId = householdId;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    /** Constructor sin fechas (compatibilidad). */
    public Invitation(Long id, Long householdId, String email, Status status) {
        this(id, householdId, email, status, null, null);
    }

    public static Invitation createNew(Long householdId, String email) {
        if (email == null || email.isBlank()) {
            throw new DomainValidationException("error.household.invitation.emailRequired", "El correo de la invitación es obligatorio");
        }
        LocalDateTime now = LocalDateTime.now();
        return new Invitation(null, householdId, email.trim().toLowerCase(), Status.PENDING,
                now, now.plusDays(VALID_DAYS));
    }

    public boolean isExpired(LocalDateTime now) {
        return expiresAt != null && now.isAfter(expiresAt);
    }

    public Invitation accept() { return changeTo(Status.ACCEPTED); }

    public Invitation decline() { return changeTo(Status.DECLINED); }

    public Invitation cancel() { return changeTo(Status.CANCELLED); }

    /** Solo una invitación pendiente puede responderse o cancelarse. */
    private Invitation changeTo(Status newStatus) {
        if (status != Status.PENDING) {
            throw new InvitationNotPendingException();
        }
        return new Invitation(id, householdId, email, newStatus, createdAt, expiresAt);
    }

    public Long getId() { return id; }
    public Long getHouseholdId() { return householdId; }
    public String getEmail() { return email; }
    public Status getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}
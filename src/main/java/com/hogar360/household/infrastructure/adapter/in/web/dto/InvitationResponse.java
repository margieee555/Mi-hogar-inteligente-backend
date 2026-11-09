package com.hogar360.household.infrastructure.adapter.in.web.dto;

import com.hogar360.household.domain.model.InvitationView;

import java.time.LocalDateTime;

public record InvitationResponse(Long id, Long householdId, String householdName, String ownerName,
                                 String email, LocalDateTime createdAt, LocalDateTime expiresAt) {

    public static InvitationResponse from(InvitationView view) {
        return new InvitationResponse(view.id(), view.householdId(), view.householdName(), view.ownerName(),
                view.email(), view.createdAt(), view.expiresAt());
    }
}
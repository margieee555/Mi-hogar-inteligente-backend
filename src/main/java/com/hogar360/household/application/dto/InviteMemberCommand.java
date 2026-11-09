package com.hogar360.household.application.dto;

public record InviteMemberCommand(Long householdId, String ownerEmail, String inviteeEmail) {
}

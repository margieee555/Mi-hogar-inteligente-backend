package com.hogar360.household.infrastructure.adapter.in.web;

import com.hogar360.household.domain.port.in.ManageMembershipUseCase;
import com.hogar360.household.infrastructure.adapter.in.web.dto.HouseholdResponse;
import com.hogar360.household.infrastructure.adapter.in.web.dto.InvitationResponse;
import com.hogar360.household.infrastructure.adapter.in.web.dto.MemberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Miembros e invitaciones del hogar. authentication.getName() es el email del JWT. */
@RestController
@RequestMapping("/api/v1")
public class MembershipController {

    private final ManageMembershipUseCase membershipUseCase;

    public MembershipController(ManageMembershipUseCase membershipUseCase) {
        this.membershipUseCase = membershipUseCase;
    }

    // ---- Miembros ----
    @GetMapping("/households/{householdId}/members")
    public ResponseEntity<List<MemberResponse>> members(@PathVariable Long householdId,
                                                        Authentication authentication) {
        return ResponseEntity.ok(membershipUseCase.listMembers(householdId, authentication.getName())
                .stream().map(MemberResponse::from).toList());
    }

    @DeleteMapping("/households/{householdId}/members/me")
    public ResponseEntity<Void> leave(@PathVariable Long householdId, Authentication authentication) {
        membershipUseCase.leave(householdId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/households/{householdId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long householdId, @PathVariable Long userId,
                                             Authentication authentication) {
        membershipUseCase.removeMember(householdId, userId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // ---- Invitaciones del hogar (dueño) ----
    @GetMapping("/households/{householdId}/invitations")
    public ResponseEntity<List<InvitationResponse>> householdInvitations(@PathVariable Long householdId,
                                                                         Authentication authentication) {
        return ResponseEntity.ok(membershipUseCase.listHouseholdInvitations(householdId, authentication.getName())
                .stream().map(InvitationResponse::from).toList());
    }

    @DeleteMapping("/households/{householdId}/invitations/{invitationId}")
    public ResponseEntity<Void> cancelInvitation(@PathVariable Long householdId, @PathVariable Long invitationId,
                                                 Authentication authentication) {
        membershipUseCase.cancelInvitation(householdId, invitationId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // ---- Invitaciones recibidas ----
    @GetMapping("/invitations/mine")
    public ResponseEntity<List<InvitationResponse>> myInvitations(Authentication authentication) {
        return ResponseEntity.ok(membershipUseCase.listMyInvitations(authentication.getName())
                .stream().map(InvitationResponse::from).toList());
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<HouseholdResponse> accept(@PathVariable Long invitationId, Authentication authentication) {
        return ResponseEntity.ok(HouseholdResponse.from(
                membershipUseCase.acceptInvitation(invitationId, authentication.getName())));
    }

    @PostMapping("/invitations/{invitationId}/decline")
    public ResponseEntity<Void> decline(@PathVariable Long invitationId, Authentication authentication) {
        membershipUseCase.declineInvitation(invitationId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
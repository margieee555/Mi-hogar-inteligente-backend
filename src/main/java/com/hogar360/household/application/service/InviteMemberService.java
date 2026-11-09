package com.hogar360.household.application.service;

import com.hogar360.household.application.dto.InviteMemberCommand;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.model.Invitation;
import com.hogar360.household.domain.port.in.InviteMemberUseCase;
import com.hogar360.household.domain.port.out.HouseholdMemberRepositoryPort;
import com.hogar360.household.domain.port.out.InvitationNotifierPort;
import com.hogar360.household.domain.port.out.InvitationRepositoryPort;
import com.hogar360.household.domain.port.out.UserLookupPort;
import com.hogar360.shared.exception.DomainValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InviteMemberService implements InviteMemberUseCase {

    private final InvitationRepositoryPort invitationRepositoryPort;
    private final HouseholdOwnershipChecker ownershipChecker;
    private final UserLookupPort userLookupPort;
    private final HouseholdMemberRepositoryPort memberRepositoryPort;
    private final InvitationNotifierPort notifierPort;

    public InviteMemberService(InvitationRepositoryPort invitationRepositoryPort,
                               HouseholdOwnershipChecker ownershipChecker,
                               UserLookupPort userLookupPort,
                               HouseholdMemberRepositoryPort memberRepositoryPort,
                               InvitationNotifierPort notifierPort) {
        this.invitationRepositoryPort = invitationRepositoryPort;
        this.ownershipChecker = ownershipChecker;
        this.userLookupPort = userLookupPort;
        this.memberRepositoryPort = memberRepositoryPort;
        this.notifierPort = notifierPort;
    }

    @Override
    public void invite(InviteMemberCommand command) {
        Household household = ownershipChecker.requireOwned(command.householdId(), command.ownerEmail());
        Invitation invitation = Invitation.createNew(household.getId(), command.inviteeEmail());

        if (invitation.getEmail().equals(command.ownerEmail().trim().toLowerCase())) {
            throw new DomainValidationException("error.invitation.self", "No puedes invitarte a ti mismo");
        }

        userLookupPort.findUserIdByEmail(invitation.getEmail()).ifPresent(userId -> {
            if (memberRepositoryPort.findByHouseholdIdAndUserId(household.getId(), userId).isPresent()) {
                throw new DomainValidationException("error.invitation.alreadyMember",
                        "Esa persona ya es miembro de tu hogar");
            }
        });

        LocalDateTime now = LocalDateTime.now();
        List<Invitation> pending = invitationRepositoryPort.findPendingByHouseholdId(household.getId()).stream()
                .filter(i -> !i.isExpired(now))
                .toList();
        if (pending.stream().anyMatch(i -> i.getEmail().equals(invitation.getEmail()))) {
            throw new DomainValidationException("error.invitation.alreadyPending",
                    "Ya hay una invitación pendiente para ese correo");
        }

        int occupied = memberRepositoryPort.findByHouseholdId(household.getId()).size() + pending.size();
        if (occupied >= household.getResidentCount()) {
            throw new DomainValidationException("error.invitation.householdFull",
                    "Tu hogar ya completó los " + household.getResidentCount() + " residentes definidos. "
                            + "Aumenta el número de residentes para invitar a más personas",
                    household.getResidentCount());
        }

        invitationRepositoryPort.save(invitation);
        notifierPort.notifyInvitation(invitation.getEmail(), household.getName(), command.ownerEmail());
    }
}
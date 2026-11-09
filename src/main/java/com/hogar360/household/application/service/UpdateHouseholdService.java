package com.hogar360.household.application.service;

import com.hogar360.household.application.dto.UpdateHouseholdCommand;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.in.UpdateHouseholdUseCase;
import com.hogar360.household.domain.port.out.HouseholdMemberRepositoryPort;
import com.hogar360.household.domain.port.out.HouseholdRepositoryPort;
import com.hogar360.household.domain.port.out.InvitationRepositoryPort;
import com.hogar360.shared.exception.DomainValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UpdateHouseholdService implements UpdateHouseholdUseCase {

    private final HouseholdRepositoryPort householdRepositoryPort;
    private final HouseholdOwnershipChecker ownershipChecker;
    private final HouseholdMemberRepositoryPort memberRepositoryPort;
    private final InvitationRepositoryPort invitationRepositoryPort;

    public UpdateHouseholdService(HouseholdRepositoryPort householdRepositoryPort,
                                  HouseholdOwnershipChecker ownershipChecker,
                                  HouseholdMemberRepositoryPort memberRepositoryPort,
                                  InvitationRepositoryPort invitationRepositoryPort) {
        this.householdRepositoryPort = householdRepositoryPort;
        this.ownershipChecker = ownershipChecker;
        this.memberRepositoryPort = memberRepositoryPort;
        this.invitationRepositoryPort = invitationRepositoryPort;
    }

    @Override
    public Household update(UpdateHouseholdCommand command) {
        Household household = ownershipChecker.requireOwned(command.householdId(), command.ownerEmail());

        // Los cupos ocupados son los miembros (dueño incluido) más las invitaciones pendientes vigentes
        LocalDateTime now = LocalDateTime.now();
        int pending = (int) invitationRepositoryPort.findPendingByHouseholdId(household.getId()).stream()
                .filter(i -> !i.isExpired(now))
                .count();
        int occupied = memberRepositoryPort.findByHouseholdId(household.getId()).size() + pending;

        if (command.residentCount() < occupied) {
            throw new DomainValidationException("error.household.residentCount.belowMembers",
                    "No puedes bajar a " + command.residentCount() + " residentes: ya hay " + occupied
                            + " entre miembros e invitaciones pendientes",
                    command.residentCount(), occupied);
        }

        return householdRepositoryPort.save(
                household.withBasicInfo(command.name(), command.type(), command.residentCount()));
    }
}
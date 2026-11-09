package com.hogar360.household.infrastructure.adapter.in.web.mapper;

import com.hogar360.household.application.dto.AddRoomsCommand;
import com.hogar360.household.application.dto.CreateHouseholdCommand;
import com.hogar360.household.application.dto.InviteMemberCommand;
import com.hogar360.household.infrastructure.adapter.in.web.dto.AddRoomsRequest;
import com.hogar360.household.infrastructure.adapter.in.web.dto.CreateHouseholdRequest;
import com.hogar360.household.infrastructure.adapter.in.web.dto.InviteMemberRequest;
import org.springframework.stereotype.Component;

/** Traduce DTOs HTTP a commands de aplicación, sumando el email del usuario autenticado. */
@Component
public class HouseholdWebMapper {

    public CreateHouseholdCommand toCommand(String ownerEmail, CreateHouseholdRequest request) {
        return new CreateHouseholdCommand(ownerEmail, request.name(), request.type(), request.residentCount());
    }

    public AddRoomsCommand toCommand(Long householdId, String ownerEmail, AddRoomsRequest request) {
        return new AddRoomsCommand(householdId, ownerEmail, request.rooms());
    }

    public InviteMemberCommand toCommand(Long householdId, String ownerEmail, InviteMemberRequest request) {
        return new InviteMemberCommand(householdId, ownerEmail, request.email());
    }
}
package com.hogar360.household.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hogar360.household.application.dto.AddRoomsCommand;
import com.hogar360.household.application.dto.CreateHouseholdCommand;
import com.hogar360.household.application.dto.InviteMemberCommand;
import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.in.AddRoomsUseCase;
import com.hogar360.household.domain.port.in.CompleteOnboardingUseCase;
import com.hogar360.household.domain.port.in.CreateHouseholdUseCase;
import com.hogar360.household.domain.port.in.GetMyHouseholdUseCase;
import com.hogar360.household.domain.port.in.InviteMemberUseCase;
import com.hogar360.household.infrastructure.adapter.in.web.dto.CreateHouseholdRequest;
import com.hogar360.household.infrastructure.adapter.in.web.dto.HouseholdResponse;

@RestController
@RequestMapping("/api/v1/households")
public class HouseholdController {

    private final CreateHouseholdUseCase createHouseholdUseCase;
    private final AddRoomsUseCase addRoomsUseCase;
    private final InviteMemberUseCase inviteMemberUseCase;
    private final CompleteOnboardingUseCase completeOnboardingUseCase;
    private final GetMyHouseholdUseCase getMyHouseholdUseCase;

    // constructor con las 5 dependencias...

    @PostMapping
    public ResponseEntity<HouseholdResponse> create(@Valid @RequestBody CreateHouseholdRequest request,
                                                      Authentication authentication) {
        String email = authentication.getName(); // viene del JwtAuthenticationFilter
        Household household = createHouseholdUseCase.create(
            new CreateHouseholdCommand(email, request.name(), request.type(), request.residentCount())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(HouseholdResponse.from(household));
    }

    @PostMapping("/{id}/rooms")
    public ResponseEntity<Void> addRooms(@PathVariable Long id, @RequestBody AddRoomsRequest request) {
        addRoomsUseCase.addRooms(new AddRoomsCommand(id, request.rooms()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/invitations")
    public ResponseEntity<Void> invite(@PathVariable Long id, @RequestBody InviteMemberRequest request) {
        inviteMemberUseCase.invite(new InviteMemberCommand(id, request.email()));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long id) {
        completeOnboardingUseCase.complete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<HouseholdResponse> me(Authentication authentication) {
        Household household = getMyHouseholdUseCase.getByOwnerEmail(authentication.getName());
        return ResponseEntity.ok(HouseholdResponse.from(household));
    }
}

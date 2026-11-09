package com.hogar360.household.infrastructure.adapter.in.web;

import com.hogar360.household.domain.model.Household;
import com.hogar360.household.domain.port.in.AddRoomsUseCase;
import com.hogar360.household.domain.port.in.CompleteOnboardingUseCase;
import com.hogar360.household.domain.port.in.CreateHouseholdUseCase;
import com.hogar360.household.domain.port.in.GetMyHouseholdUseCase;
import com.hogar360.household.domain.port.in.InviteMemberUseCase;
import com.hogar360.household.infrastructure.adapter.in.web.dto.AddRoomsRequest;
import com.hogar360.household.infrastructure.adapter.in.web.dto.CreateHouseholdRequest;
import com.hogar360.household.infrastructure.adapter.in.web.dto.HouseholdResponse;
import com.hogar360.household.infrastructure.adapter.in.web.dto.InviteMemberRequest;
import com.hogar360.household.infrastructure.adapter.in.web.mapper.HouseholdWebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada HTTP del onboarding. Protegido por el JwtAuthenticationFilter:
 * authentication.getName() devuelve el email que se guardó como subject del JWT.
 */
@RestController
@RequestMapping("/api/v1/households")
public class HouseholdController {

    private final CreateHouseholdUseCase createHouseholdUseCase;
    private final AddRoomsUseCase addRoomsUseCase;
    private final InviteMemberUseCase inviteMemberUseCase;
    private final CompleteOnboardingUseCase completeOnboardingUseCase;
    private final GetMyHouseholdUseCase getMyHouseholdUseCase;
    private final HouseholdWebMapper mapper;

    public HouseholdController(CreateHouseholdUseCase createHouseholdUseCase,
                               AddRoomsUseCase addRoomsUseCase,
                               InviteMemberUseCase inviteMemberUseCase,
                               CompleteOnboardingUseCase completeOnboardingUseCase,
                               GetMyHouseholdUseCase getMyHouseholdUseCase,
                               HouseholdWebMapper mapper) {
        this.createHouseholdUseCase = createHouseholdUseCase;
        this.addRoomsUseCase = addRoomsUseCase;
        this.inviteMemberUseCase = inviteMemberUseCase;
        this.completeOnboardingUseCase = completeOnboardingUseCase;
        this.getMyHouseholdUseCase = getMyHouseholdUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<HouseholdResponse> create(@Valid @RequestBody CreateHouseholdRequest request,
                                                    Authentication authentication) {
        Household household = createHouseholdUseCase.create(mapper.toCommand(authentication.getName(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(HouseholdResponse.from(household));
    }

    @PostMapping("/{id}/rooms")
    public ResponseEntity<HouseholdResponse> addRooms(@PathVariable Long id,
                                                      @Valid @RequestBody AddRoomsRequest request,
                                                      Authentication authentication) {
        Household household = addRoomsUseCase.addRooms(mapper.toCommand(id, authentication.getName(), request));
        return ResponseEntity.ok(HouseholdResponse.from(household));
    }

    @PostMapping("/{id}/invitations")
    public ResponseEntity<Void> invite(@PathVariable Long id,
                                       @Valid @RequestBody InviteMemberRequest request,
                                       Authentication authentication) {
        inviteMemberUseCase.invite(mapper.toCommand(id, authentication.getName(), request));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<HouseholdResponse> complete(@PathVariable Long id, Authentication authentication) {
        Household household = completeOnboardingUseCase.complete(id, authentication.getName());
        return ResponseEntity.ok(HouseholdResponse.from(household));
    }

    @GetMapping("/me")
    public ResponseEntity<HouseholdResponse> me(Authentication authentication) {
        Household household = getMyHouseholdUseCase.getByOwnerEmail(authentication.getName());
        return ResponseEntity.ok(HouseholdResponse.from(household));
    }
}
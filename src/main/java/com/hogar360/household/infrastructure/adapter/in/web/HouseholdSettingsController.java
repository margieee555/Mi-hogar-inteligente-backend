package com.hogar360.household.infrastructure.adapter.in.web;

import com.hogar360.household.application.dto.UpdateHouseholdCommand;
import com.hogar360.household.domain.port.in.UpdateHouseholdUseCase;
import com.hogar360.household.infrastructure.adapter.in.web.dto.CreateHouseholdRequest;
import com.hogar360.household.infrastructure.adapter.in.web.dto.HouseholdResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Edición de los datos del hogar: solo el dueño (authentication.getName() es el email del JWT). */
@RestController
@RequestMapping("/api/v1/households")
public class HouseholdSettingsController {

    private final UpdateHouseholdUseCase updateHouseholdUseCase;

    public HouseholdSettingsController(UpdateHouseholdUseCase updateHouseholdUseCase) {
        this.updateHouseholdUseCase = updateHouseholdUseCase;
    }

    @PutMapping("/{id}")
    public ResponseEntity<HouseholdResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody CreateHouseholdRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(HouseholdResponse.from(updateHouseholdUseCase.update(
                new UpdateHouseholdCommand(id, authentication.getName(),
                        request.name(), request.type(), request.residentCount()))));
    }
}

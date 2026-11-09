package com.hogar360.household.infrastructure.adapter.in.web.dto;

import com.hogar360.household.domain.model.HomeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateHouseholdRequest(

        @NotBlank(message = "El nombre del hogar es obligatorio")
        String name,

        @NotNull(message = "El tipo de vivienda es obligatorio")
        HomeType type,

        @Min(value = 1, message = "Debe haber al menos 1 residente")
        int residentCount
) {
}

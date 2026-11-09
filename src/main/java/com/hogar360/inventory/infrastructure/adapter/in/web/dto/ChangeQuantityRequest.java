package com.hogar360.inventory.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ChangeQuantityRequest(

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.0", message = "La cantidad no puede ser negativa")
        @Digits(integer = 8, fraction = 2, message = "La cantidad admite hasta 8 enteros y 2 decimales")
        BigDecimal quantity
) {
}
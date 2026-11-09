package com.hogar360.inventory.infrastructure.adapter.in.web.dto;

import com.hogar360.inventory.domain.model.InventoryCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Se usa igual para crear (POST) y para editar (PUT). */
public record InventoryItemRequest(

        @NotBlank(message = "El nombre del artículo es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String name,

        @NotNull(message = "La categoría es obligatoria")
        InventoryCategory category,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.0", message = "La cantidad no puede ser negativa")
        @Digits(integer = 8, fraction = 2, message = "La cantidad admite hasta 8 enteros y 2 decimales")
        BigDecimal quantity,

        @NotBlank(message = "La unidad es obligatoria")
        @Size(max = 20, message = "La unidad no puede superar 20 caracteres")
        String unit,

        @NotNull(message = "El stock ideal es obligatorio")
        @DecimalMin(value = "0.01", message = "El stock ideal debe ser mayor a cero")
        @Digits(integer = 8, fraction = 2, message = "El stock ideal admite hasta 8 enteros y 2 decimales")
        BigDecimal targetQuantity,

        LocalDate expirationDate
) {
}
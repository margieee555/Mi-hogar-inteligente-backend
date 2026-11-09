package com.hogar360.finance.infrastructure.adapter.in.web.dto;

import com.hogar360.finance.domain.model.TransactionCategory;
import com.hogar360.finance.domain.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Se usa igual para crear (POST) y para editar (PUT). */
public record TransactionRequest(

        @NotNull(message = "El tipo de movimiento es obligatorio")
        TransactionType type,

        @NotNull(message = "La categoría es obligatoria")
        TransactionCategory category,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        @Digits(integer = 13, fraction = 2, message = "El monto admite hasta 13 enteros y 2 decimales")
        BigDecimal amount,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String description,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate date
) {
}
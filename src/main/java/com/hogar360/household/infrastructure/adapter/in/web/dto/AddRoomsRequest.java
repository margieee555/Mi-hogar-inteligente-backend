package com.hogar360.household.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AddRoomsRequest(
        @NotEmpty(message = "Debes seleccionar al menos un espacio")
        List<@NotBlank(message = "El nombre del espacio no puede estar vacío")
             @Size(max = 100, message = "El nombre del espacio no puede superar 100 caracteres") String> rooms
) {
}

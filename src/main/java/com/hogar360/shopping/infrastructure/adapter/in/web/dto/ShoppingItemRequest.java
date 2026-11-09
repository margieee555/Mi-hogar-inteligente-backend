package com.hogar360.shopping.infrastructure.adapter.in.web.dto;

import com.hogar360.shopping.domain.model.ShoppingCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ShoppingItemRequest(

        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String name,

        @NotNull(message = "La categoría es obligatoria")
        ShoppingCategory category,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        @Max(value = 999, message = "La cantidad no puede superar 999")
        Integer quantity
) {
}

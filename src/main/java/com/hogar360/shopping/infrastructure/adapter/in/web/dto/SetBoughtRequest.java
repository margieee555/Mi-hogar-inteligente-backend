package com.hogar360.shopping.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record SetBoughtRequest(

        @NotNull(message = "Indica si el producto fue comprado")
        Boolean bought
) {
}
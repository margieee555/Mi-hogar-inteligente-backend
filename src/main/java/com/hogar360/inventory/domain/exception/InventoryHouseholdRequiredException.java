package com.hogar360.inventory.domain.exception;

public class InventoryHouseholdRequiredException extends RuntimeException {

    public InventoryHouseholdRequiredException() {
        super("Debes configurar tu hogar antes de usar el inventario");
    }
}
package com.hogar360.inventory.domain.exception;

public class InventoryItemNotFoundException extends RuntimeException {

    private final Long itemId;

    public InventoryItemNotFoundException(Long id) {
        super("No se encontró el artículo con id: " + id);
        this.itemId = id;
    }

    public Long getItemId() { return itemId; }
}

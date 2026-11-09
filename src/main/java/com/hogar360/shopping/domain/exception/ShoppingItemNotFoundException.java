package com.hogar360.shopping.domain.exception;

public class ShoppingItemNotFoundException extends RuntimeException {

    private final Long itemId;

    public ShoppingItemNotFoundException(Long id) {
        super("No se encontró el producto con id: " + id);
        this.itemId = id;
    }

    public Long getItemId() { return itemId; }
}

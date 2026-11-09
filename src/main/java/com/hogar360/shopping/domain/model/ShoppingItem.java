package com.hogar360.shopping.domain.model;

import com.hogar360.shared.exception.DomainValidationException;

import java.time.LocalDateTime;

/**
 * Producto de la lista de compras del hogar. Inmutable y sin dependencias de frameworks.
 * El constructor público es "crudo" (lo usa el mapper al leer de la BD);
 * createNew, withQuantity y withBought son los que validan y normalizan.
 */
public class ShoppingItem {

    public static final int MAX_NAME_LENGTH = 120;
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 999;

    private final Long id;
    private final Long householdId;
    private final String name;
    private final ShoppingCategory category;
    private final int quantity;
    private final boolean bought;
    private final LocalDateTime boughtAt;
    private final LocalDateTime createdAt;
    private final Integer restockedQuantity;

    public ShoppingItem(Long id, Long householdId, String name, ShoppingCategory category,
                        int quantity, boolean bought, LocalDateTime boughtAt, LocalDateTime createdAt,
                        Integer restockedQuantity) {
        this.id = id;
        this.householdId = householdId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.bought = bought;
        this.boughtAt = boughtAt;
        this.createdAt = createdAt;
        this.restockedQuantity = restockedQuantity;
    }

    /** Constructor sin reposición (producto que no ha repuesto el inventario). */
    public ShoppingItem(Long id, Long householdId, String name, ShoppingCategory category,
                        int quantity, boolean bought, LocalDateTime boughtAt, LocalDateTime createdAt) {
        this(id, householdId, name, category, quantity, bought, boughtAt, createdAt, null);
    }

    public static ShoppingItem createNew(Long householdId, String name, ShoppingCategory category, int quantity) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("error.shopping.name.required", "El nombre del producto es obligatorio");
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new DomainValidationException("error.shopping.name.tooLong",
                    "El nombre no puede superar " + MAX_NAME_LENGTH + " caracteres", MAX_NAME_LENGTH);
        }
        if (category == null) {
            throw new DomainValidationException("error.shopping.category.required", "La categoría es obligatoria");
        }
        validateQuantity(quantity);
        return new ShoppingItem(null, householdId, name.trim(), category, quantity, false, null,
                LocalDateTime.now(), null);
    }

    public ShoppingItem withQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        return new ShoppingItem(id, householdId, name, category, newQuantity, bought, boughtAt, createdAt,
                restockedQuantity);
    }

    /**
     * Marcar guarda la fecha de compra; desmarcar la borra y olvida la reposición
     * (el servicio ya la deshizo en el inventario antes de llegar aquí).
     */
    public ShoppingItem withBought(boolean newBought) {
        LocalDateTime newBoughtAt = null;
        Integer newRestocked = null;
        if (newBought) {
            newBoughtAt = boughtAt != null ? boughtAt : LocalDateTime.now();
            newRestocked = restockedQuantity;
        }
        return new ShoppingItem(id, householdId, name, category, quantity, newBought, newBoughtAt, createdAt,
                newRestocked);
    }

    /** Guarda cuánto se sumó al inventario al comprar este producto. */
    public ShoppingItem withRestockedQuantity(Integer amount) {
        return new ShoppingItem(id, householdId, name, category, quantity, bought, boughtAt, createdAt, amount);
    }

    private static void validateQuantity(int quantity) {
        if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
            throw new DomainValidationException("error.shopping.quantity.range",
                    "La cantidad debe estar entre " + MIN_QUANTITY + " y " + MAX_QUANTITY,
                    MIN_QUANTITY, MAX_QUANTITY);
        }
    }

    public Long getId() { return id; }
    public Long getHouseholdId() { return householdId; }
    public String getName() { return name; }
    public ShoppingCategory getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public boolean isBought() { return bought; }
    public LocalDateTime getBoughtAt() { return boughtAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Integer getRestockedQuantity() { return restockedQuantity; }
}
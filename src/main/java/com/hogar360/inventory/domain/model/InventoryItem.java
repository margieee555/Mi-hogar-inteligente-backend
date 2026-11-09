package com.hogar360.inventory.domain.model;

import com.hogar360.shared.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Artículo del inventario del hogar. Inmutable y sin dependencias de frameworks.
 * El constructor público es "crudo" (lo usa el mapper al leer de la BD);
 * createNew, update y withQuantity son los que validan y normalizan.
 */
public class InventoryItem {

    public static final int MAX_NAME_LENGTH = 120;
    public static final int MAX_UNIT_LENGTH = 20;
    public static final int LOW_STOCK_PERCENT = 25;
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999999.99");

    private final Long id;
    private final Long householdId;
    private final String name;
    private final InventoryCategory category;
    private final BigDecimal quantity;
    private final String unit;
    private final BigDecimal targetQuantity;
    private final LocalDate expirationDate;
    private final LocalDateTime createdAt;

    public InventoryItem(Long id, Long householdId, String name, InventoryCategory category,
                         BigDecimal quantity, String unit, BigDecimal targetQuantity,
                         LocalDate expirationDate, LocalDateTime createdAt) {
        this.id = id;
        this.householdId = householdId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.targetQuantity = targetQuantity;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
    }

    public static InventoryItem createNew(Long householdId, String name, InventoryCategory category,
                                          BigDecimal quantity, String unit, BigDecimal targetQuantity,
                                          LocalDate expirationDate) {
        validate(name, category, quantity, unit, targetQuantity);
        return new InventoryItem(null, householdId, name.trim(), category, normalizeAmount(quantity),
                unit.trim(), normalizeAmount(targetQuantity), expirationDate, LocalDateTime.now());
    }

    /** Devuelve una copia con los datos nuevos, conservando id, hogar y fecha de creación. */
    public InventoryItem update(String newName, InventoryCategory newCategory, BigDecimal newQuantity,
                                String newUnit, BigDecimal newTargetQuantity, LocalDate newExpirationDate) {
        validate(newName, newCategory, newQuantity, newUnit, newTargetQuantity);
        return new InventoryItem(id, householdId, newName.trim(), newCategory, normalizeAmount(newQuantity),
                newUnit.trim(), normalizeAmount(newTargetQuantity), newExpirationDate, createdAt);
    }

    /** Ajuste rápido de cantidad (consumir o reponer). */
    public InventoryItem withQuantity(BigDecimal newQuantity) {
        validateQuantity(newQuantity);
        return new InventoryItem(id, householdId, name, category, normalizeAmount(newQuantity),
                unit, targetQuantity, expirationDate, createdAt);
    }

    /** Suma unidades (reposición por una compra), sin pasar del máximo permitido. */
    public InventoryItem restock(BigDecimal amount) {
        return withQuantity(quantity.add(amount).min(MAX_AMOUNT));
    }

    /** Resta unidades (deshacer una reposición), sin bajar de cero. */
    public InventoryItem consume(BigDecimal amount) {
        return withQuantity(quantity.subtract(amount).max(BigDecimal.ZERO));
    }
    
    /** Nivel de stock respecto al ideal, de 0 a 100. */
    public int stockPercent() {
        if (targetQuantity == null || targetQuantity.signum() <= 0 || quantity == null) {
            return 0;
        }
        int percent = quantity.multiply(BigDecimal.valueOf(100))
                .divide(targetQuantity, 0, RoundingMode.HALF_UP)
                .intValue();
        return Math.min(100, percent);
    }

    public boolean isLowStock() {
        return stockPercent() <= LOW_STOCK_PERCENT;
    }

    private static void validate(String name, InventoryCategory category, BigDecimal quantity,
                                 String unit, BigDecimal targetQuantity) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("error.inventory.name.required", "El nombre del artículo es obligatorio");
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new DomainValidationException("error.inventory.name.tooLong",
                    "El nombre no puede superar " + MAX_NAME_LENGTH + " caracteres", MAX_NAME_LENGTH);
        }
        if (category == null) {
            throw new DomainValidationException("error.inventory.category.required", "La categoría es obligatoria");
        }
        validateQuantity(quantity);
        if (unit == null || unit.isBlank()) {
            throw new DomainValidationException("error.inventory.unit.required", "La unidad es obligatoria");
        }
        if (unit.trim().length() > MAX_UNIT_LENGTH) {
            throw new DomainValidationException("error.inventory.unit.tooLong",
                    "La unidad no puede superar " + MAX_UNIT_LENGTH + " caracteres", MAX_UNIT_LENGTH);
        }
        if (targetQuantity == null || targetQuantity.signum() <= 0) {
            throw new DomainValidationException("error.inventory.target.positive",
                    "El stock ideal debe ser mayor a cero");
        }
        if (targetQuantity.compareTo(MAX_AMOUNT) > 0) {
            throw new DomainValidationException("error.inventory.amount.max", "La cantidad supera el máximo permitido");
        }
    }

    private static void validateQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.signum() < 0) {
            throw new DomainValidationException("error.inventory.quantity.invalid",
                    "La cantidad no puede ser negativa");
        }
        if (quantity.compareTo(MAX_AMOUNT) > 0) {
            throw new DomainValidationException("error.inventory.amount.max", "La cantidad supera el máximo permitido");
        }
    }

    private static BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public Long getHouseholdId() { return householdId; }
    public String getName() { return name; }
    public InventoryCategory getCategory() { return category; }
    public BigDecimal getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public BigDecimal getTargetQuantity() { return targetQuantity; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
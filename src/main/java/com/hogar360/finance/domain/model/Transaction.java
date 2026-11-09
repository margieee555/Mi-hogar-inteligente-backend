package com.hogar360.finance.domain.model;

import com.hogar360.shared.exception.DomainValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Movimiento financiero del hogar (gasto o ingreso). Inmutable y sin dependencias de frameworks.
 * El constructor público es "crudo" (lo usa el mapper al leer de la BD);
 * createNew y update son los que validan y normalizan.
 */
public class Transaction {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("9999999999999.99");
    private static final int MAX_DESCRIPTION_LENGTH = 255;

    private final Long id;
    private final Long householdId;
    private final TransactionType type;
    private final TransactionCategory category;
    private final BigDecimal amount;
    private final String description;
    private final LocalDate date;
    private final LocalDateTime createdAt;

    public Transaction(Long id, Long householdId, TransactionType type, TransactionCategory category,
                       BigDecimal amount, String description, LocalDate date, LocalDateTime createdAt) {
        this.id = id;
        this.householdId = householdId;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.createdAt = createdAt;
    }

    public static Transaction createNew(Long householdId, TransactionType type, TransactionCategory category,
                                        BigDecimal amount, String description, LocalDate date) {
        validate(type, category, amount, description, date);
        return new Transaction(null, householdId, type, category, normalizeAmount(amount),
                normalizeDescription(description), date, LocalDateTime.now());
    }

    /** Devuelve una copia con los datos nuevos, conservando id, hogar y fecha de creación. */
    public Transaction update(TransactionType newType, TransactionCategory newCategory, BigDecimal newAmount,
                              String newDescription, LocalDate newDate) {
        validate(newType, newCategory, newAmount, newDescription, newDate);
        return new Transaction(id, householdId, newType, newCategory, normalizeAmount(newAmount),
                normalizeDescription(newDescription), newDate, createdAt);
    }

    private static void validate(TransactionType type, TransactionCategory category, BigDecimal amount,
                                 String description, LocalDate date) {
        if (type == null) {
            throw new DomainValidationException("error.finance.type.required", "El tipo de movimiento es obligatorio");
        }
        if (category == null) {
            throw new DomainValidationException("error.finance.category.required", "La categoría es obligatoria");
        }
        if (category.getType() != type) {
            throw new DomainValidationException("error.finance.category.mismatch",
                    "La categoría '" + category.getLabel() + "' no corresponde a un movimiento de tipo " + type);
        }
        if (amount == null || amount.signum() <= 0) {
            throw new DomainValidationException("error.finance.amount.positive", "El monto debe ser mayor a cero");
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new DomainValidationException("error.finance.amount.max", "El monto supera el máximo permitido");
        }
        if (date == null) {
            throw new DomainValidationException("error.finance.date.required", "La fecha es obligatoria");
        }
        if (description != null && description.trim().length() > MAX_DESCRIPTION_LENGTH) {
            throw new DomainValidationException("error.finance.description.tooLong",
                    "La descripción no puede superar " + MAX_DESCRIPTION_LENGTH + " caracteres",
                    MAX_DESCRIPTION_LENGTH);
        }
    }

    private static BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }

    public Long getId() { return id; }
    public Long getHouseholdId() { return householdId; }
    public TransactionType getType() { return type; }
    public TransactionCategory getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
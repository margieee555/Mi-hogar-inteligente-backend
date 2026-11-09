package com.hogar360.inventory.infrastructure.adapter.out.persistence.entity;

import com.hogar360.inventory.domain.model.InventoryCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "household_id", nullable = false)
    private Long householdId;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InventoryCategory category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "target_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetQuantity;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected InventoryItemEntity() {
        // requerido por JPA
    }

    public InventoryItemEntity(Long id, Long householdId, String name, InventoryCategory category,
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

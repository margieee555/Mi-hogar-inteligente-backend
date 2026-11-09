package com.hogar360.shopping.infrastructure.adapter.out.persistence.entity;

import com.hogar360.shopping.domain.model.ShoppingCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "shopping_items")
public class ShoppingItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "household_id", nullable = false)
    private Long householdId;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShoppingCategory category;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean bought;

    @Column(name = "bought_at")
    private LocalDateTime boughtAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "restocked_quantity")
    private Integer restockedQuantity;

    protected ShoppingItemEntity() {
        // requerido por JPA
    }

    public ShoppingItemEntity(Long id, Long householdId, String name, ShoppingCategory category,
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
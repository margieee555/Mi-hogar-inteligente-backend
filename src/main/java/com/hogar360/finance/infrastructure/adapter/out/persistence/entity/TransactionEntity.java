package com.hogar360.finance.infrastructure.adapter.out.persistence.entity;

import com.hogar360.finance.domain.model.TransactionCategory;
import com.hogar360.finance.domain.model.TransactionType;
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
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "household_id", nullable = false)
    private Long householdId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TransactionCategory category;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected TransactionEntity() {
        // requerido por JPA
    }

    public TransactionEntity(Long id, Long householdId, TransactionType type, TransactionCategory category,
                             BigDecimal amount, String description, LocalDate transactionDate,
                             LocalDateTime createdAt) {
        this.id = id;
        this.householdId = householdId;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getHouseholdId() { return householdId; }
    public TransactionType getType() { return type; }
    public TransactionCategory getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
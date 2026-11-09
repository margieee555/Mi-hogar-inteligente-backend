package com.hogar360.household.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private HouseholdEntity household;

    @Column(nullable = false, length = 100)
    private String name;

    protected RoomEntity() {
        // requerido por JPA
    }

    public RoomEntity(HouseholdEntity household, String name) {
        this.household = household;
        this.name = name;
    }

    public Long getId() { return id; }
    public HouseholdEntity getHousehold() { return household; }
    public String getName() { return name; }
}
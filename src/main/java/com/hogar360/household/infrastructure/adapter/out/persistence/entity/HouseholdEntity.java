package com.hogar360.household.infrastructure.adapter.out.persistence.entity;

import com.hogar360.household.domain.model.HomeType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "households")
public class HouseholdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private HomeType type;

    @Column(name = "resident_count", nullable = false)
    private int residentCount;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<RoomEntity> rooms = new ArrayList<>();

    protected HouseholdEntity() {
        // requerido por JPA
    }

    public HouseholdEntity(Long id, Long ownerUserId, String name, HomeType type, int residentCount,
                           boolean onboardingCompleted, LocalDateTime createdAt) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.type = type;
        this.residentCount = residentCount;
        this.onboardingCompleted = onboardingCompleted;
        this.createdAt = createdAt;
    }

    public void replaceRooms(List<RoomEntity> newRooms) {
        this.rooms.clear();
        this.rooms.addAll(newRooms);
    }

    public Long getId() { return id; }
    public Long getOwnerUserId() { return ownerUserId; }
    public String getName() { return name; }
    public HomeType getType() { return type; }
    public int getResidentCount() { return residentCount; }
    public boolean isOnboardingCompleted() { return onboardingCompleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<RoomEntity> getRooms() { return rooms; }
}
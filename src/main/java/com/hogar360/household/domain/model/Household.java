package com.hogar360.household.domain.model;

import java.time.LocalDateTime;

public class Household {
    private final Long id;
    private final Long ownerUserId;
    private final String name;
    private final HomeType type;
    private final int residentCount;
    private final boolean onboardingCompleted;
    private final LocalDateTime createdAt;

public Household(Long id, Long ownerUserId, String name, HomeType type, int residentCount, boolean onboardingCompleted, LocalDateTime createdAt) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.type = type;
        this.residentCount = residentCount;
        this.onboardingCompleted = onboardingCompleted;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getOwnerUserId() { return ownerUserId; }
    public String getName() { return name; }
    public HomeType getType() { return type; }
    public int getResidentCount() { return residentCount; }
    public boolean isOnboardingCompleted() { return onboardingCompleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }   
}


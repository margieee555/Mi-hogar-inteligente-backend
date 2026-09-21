package com.hogar360.household.domain.model;

public class Room {
    private final Long id;
    private final Long householdId;
    private final String name;

    public Room(Long id, Long householdId, String name) {
        this.id = id;
        this.householdId = householdId;
        this.name = name;
    }

    public Long getId() { return id; }
    public Long getHouseholdId() { return householdId; }
    public String getName() { return name; }   
}

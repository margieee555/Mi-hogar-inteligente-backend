package com.hogar360.household.infrastructure.adapter.out.persistence.entity;

import com.hogar360.household.domain.model.MemberRole;
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
@Table(name = "household_members")
public class HouseholdMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "household_id", nullable = false)
    private Long householdId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    protected HouseholdMemberEntity() {
        // requerido por JPA
    }

    public HouseholdMemberEntity(Long id, Long householdId, Long userId, MemberRole role, LocalDateTime joinedAt) {
        this.id = id;
        this.householdId = householdId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public Long getId() { return id; }
    public Long getHouseholdId() { return householdId; }
    public Long getUserId() { return userId; }
    public MemberRole getRole() { return role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
}

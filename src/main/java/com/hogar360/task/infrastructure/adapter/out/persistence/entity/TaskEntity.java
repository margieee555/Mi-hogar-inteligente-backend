package com.hogar360.task.infrastructure.adapter.out.persistence.entity;

import com.hogar360.task.domain.model.TaskPriority;
import com.hogar360.task.domain.model.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "household_id", nullable = false)
    private Long householdId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority;

    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected TaskEntity() {
        // requerido por JPA
    }

    public TaskEntity(Long id, Long householdId, String title, String description, TaskStatus status,
                      TaskPriority priority, Long assignedUserId, LocalDate dueDate,
                      LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.householdId = householdId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assignedUserId = assignedUserId;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public Long getId() { return id; }
    public Long getHouseholdId() { return householdId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public Long getAssignedUserId() { return assignedUserId; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
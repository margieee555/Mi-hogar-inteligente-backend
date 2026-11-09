package com.hogar360.task.domain.model;

import com.hogar360.shared.exception.DomainValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tarea del hogar. Inmutable y sin dependencias de frameworks.
 * El constructor público es "crudo" (lo usa el mapper al leer de la BD);
 * createNew y changeStatus son los que validan y normalizan.
 */
public class Task {

    public static final int MAX_TITLE_LENGTH = 150;
    public static final int MAX_DESCRIPTION_LENGTH = 500;

    private final Long id;
    private final Long householdId;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final TaskPriority priority;
    private final Long assignedUserId;
    private final LocalDate dueDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime completedAt;

    public Task(Long id, Long householdId, String title, String description, TaskStatus status,
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

    public static Task createNew(Long householdId, String title, String description, TaskPriority priority,
                                 Long assignedUserId, LocalDate dueDate) {
        validate(title, description, priority);
        return new Task(null, householdId, title.trim(), normalize(description), TaskStatus.PENDING,
                priority, assignedUserId, dueDate, LocalDateTime.now(), null);
    }

    /** Devuelve una copia con el nuevo estado. Completar guarda la fecha; reabrir la borra. */
    public Task changeStatus(TaskStatus newStatus) {
        if (newStatus == null) {
            throw new DomainValidationException("error.task.status.required", "El estado de la tarea es obligatorio");
        }
        LocalDateTime newCompletedAt = null;
        if (newStatus == TaskStatus.COMPLETED) {
            newCompletedAt = completedAt != null ? completedAt : LocalDateTime.now();
        }
        return new Task(id, householdId, title, description, newStatus, priority, assignedUserId,
                dueDate, createdAt, newCompletedAt);
    }

    private static void validate(String title, String description, TaskPriority priority) {
        if (title == null || title.isBlank()) {
            throw new DomainValidationException("error.task.title.required", "El título de la tarea es obligatorio");
        }
        if (title.trim().length() > MAX_TITLE_LENGTH) {
            throw new DomainValidationException("error.task.title.tooLong",
                    "El título no puede superar " + MAX_TITLE_LENGTH + " caracteres", MAX_TITLE_LENGTH);
        }
        if (description != null && description.trim().length() > MAX_DESCRIPTION_LENGTH) {
            throw new DomainValidationException("error.task.description.tooLong",
                    "La descripción no puede superar " + MAX_DESCRIPTION_LENGTH + " caracteres",
                    MAX_DESCRIPTION_LENGTH);
        }
        if (priority == null) {
            throw new DomainValidationException("error.task.priority.required", "La prioridad es obligatoria");
        }
    }

    private static String normalize(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.trim();
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
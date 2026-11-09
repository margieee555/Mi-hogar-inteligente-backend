package com.hogar360.task.infrastructure.adapter.in.web.dto;

import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskPriority;
import com.hogar360.task.domain.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Long assignedUserId,
        String assignedTo,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {
    /** members: id de usuario -> nombre, de los miembros actuales del hogar. Quien ya no es miembro aparece sin responsable. */
    public static TaskResponse from(Task task, Map<Long, String> members) {
        Long userId = task.getAssignedUserId();
        boolean isMember = userId != null && members.containsKey(userId);
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                isMember ? userId : null,
                isMember ? members.get(userId) : null,
                task.getDueDate(),
                task.getCreatedAt(),
                task.getCompletedAt()
        );
    }
}
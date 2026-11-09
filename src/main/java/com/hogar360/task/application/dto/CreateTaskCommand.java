package com.hogar360.task.application.dto;

import com.hogar360.task.domain.model.TaskPriority;

import java.time.LocalDate;

public record CreateTaskCommand(
        String ownerEmail,
        String title,
        String description,
        TaskPriority priority,
        Long assignedUserId,
        LocalDate dueDate
) {
}
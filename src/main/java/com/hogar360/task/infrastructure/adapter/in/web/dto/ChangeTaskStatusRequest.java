package com.hogar360.task.infrastructure.adapter.in.web.dto;

import com.hogar360.task.domain.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeTaskStatusRequest(

        @NotNull(message = "El estado de la tarea es obligatorio")
        TaskStatus status
) {
}
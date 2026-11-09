package com.hogar360.task.infrastructure.adapter.in.web.dto;

import com.hogar360.task.domain.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(

        @NotBlank(message = "El título de la tarea es obligatorio")
        @Size(max = 150, message = "El título no puede superar 150 caracteres")
        String title,

        @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
        String description,

        @NotNull(message = "La prioridad es obligatoria")
        TaskPriority priority,

        /** Id del usuario responsable (debe ser miembro del hogar); null = sin responsable. */
        Long assignedUserId,

        LocalDate dueDate
) {
}
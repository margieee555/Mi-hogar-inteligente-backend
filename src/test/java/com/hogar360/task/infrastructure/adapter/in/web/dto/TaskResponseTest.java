package com.hogar360.task.infrastructure.adapter.in.web.dto;

import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskPriority;
import com.hogar360.task.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TaskResponseTest {

    private static Task taskAssignedTo(Long userId) {
        return new Task(1L, 5L, "Limpiar", null, TaskStatus.PENDING, TaskPriority.HIGH,
                userId, null, LocalDateTime.now(), null);
    }

    @Test
    void incluyeElNombreDelResponsableSiEsMiembro() {
        TaskResponse response = TaskResponse.from(taskAssignedTo(3L), Map.of(3L, "Luis"));

        assertThat(response.assignedUserId()).isEqualTo(3L);
        assertThat(response.assignedTo()).isEqualTo("Luis");
    }

    @Test
    void sinResponsableQuedaVacio() {
        TaskResponse response = TaskResponse.from(taskAssignedTo(null), Map.of(3L, "Luis"));

        assertThat(response.assignedUserId()).isNull();
        assertThat(response.assignedTo()).isNull();
    }

    @Test
    void siElResponsableYaNoEsMiembroLaTareaApareceSinResponsable() {
        TaskResponse response = TaskResponse.from(taskAssignedTo(9L), Map.of(3L, "Luis"));

        assertThat(response.assignedUserId()).isNull();
        assertThat(response.assignedTo()).isNull();
    }
}

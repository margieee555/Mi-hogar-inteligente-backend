package com.hogar360.task.infrastructure.adapter.out.statistics;

import com.hogar360.statistics.domain.model.TaskCounts;
import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskPriority;
import com.hogar360.task.domain.model.TaskStatus;
import com.hogar360.task.domain.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticsTasksAdapterTest {

    private static Task task(TaskStatus status) {
        return new Task(null, 7L, "Tarea", null, status, TaskPriority.MEDIUM, null, null,
                LocalDateTime.now(), null);
    }

    @Test
    void cuentaLasTareasPorEstado() {
        TaskRepositoryPort repository = mock(TaskRepositoryPort.class);
        when(repository.findByHousehold(7L, null)).thenReturn(List.of(
                task(TaskStatus.PENDING), task(TaskStatus.PENDING),
                task(TaskStatus.IN_PROGRESS), task(TaskStatus.COMPLETED)));

        TaskCounts counts = new StatisticsTasksAdapter(repository).countTasks(7L);

        assertEquals(new TaskCounts(2, 1, 1), counts);
    }
}
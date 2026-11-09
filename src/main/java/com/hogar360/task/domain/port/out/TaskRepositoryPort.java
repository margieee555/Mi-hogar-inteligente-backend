package com.hogar360.task.domain.port.out;

import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);

    /** Busca por id DENTRO del hogar: una tarea de otro hogar se ve como inexistente. */
    Optional<Task> findByIdAndHouseholdId(Long id, Long householdId);

    /** Tareas del hogar; status null = todas. Con fecha límite más próxima primero, sin fecha al final. */
    List<Task> findByHousehold(Long householdId, TaskStatus status);

    void deleteById(Long id);
}
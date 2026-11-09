package com.hogar360.task.domain.port.in;

import com.hogar360.task.application.dto.CreateTaskCommand;
import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskStatus;

import java.util.List;
import java.util.Map;

public interface ManageTasksUseCase {
    Task create(CreateTaskCommand command);

    List<Task> list(String ownerEmail, TaskStatus status);

    Task changeStatus(Long taskId, String ownerEmail, TaskStatus newStatus);

    void delete(Long taskId, String ownerEmail);

    /** Miembros del hogar del usuario (id -> nombre): sirve para mostrar y validar responsables. */
    Map<Long, String> assigneeNames(String ownerEmail);
}
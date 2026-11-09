package com.hogar360.task.application.service;

import com.hogar360.shared.exception.DomainValidationException;
import com.hogar360.task.application.dto.CreateTaskCommand;
import com.hogar360.task.domain.exception.TaskNotFoundException;
import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskStatus;
import com.hogar360.task.domain.port.in.ManageTasksUseCase;
import com.hogar360.task.domain.port.out.TaskAssigneeLookupPort;
import com.hogar360.task.domain.port.out.TaskRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TaskService implements ManageTasksUseCase {

    private final TaskRepositoryPort repository;
    private final TaskHouseholdResolver householdResolver;
    private final TaskAssigneeLookupPort assigneeLookup;

    public TaskService(TaskRepositoryPort repository, TaskHouseholdResolver householdResolver,
                       TaskAssigneeLookupPort assigneeLookup) {
        this.repository = repository;
        this.householdResolver = householdResolver;
        this.assigneeLookup = assigneeLookup;
    }

    @Override
    public Task create(CreateTaskCommand command) {
        Long householdId = householdResolver.requireHouseholdId(command.ownerEmail());
        requireMember(householdId, command.ownerEmail(), command.assignedUserId());

        Task task = Task.createNew(householdId, command.title(), command.description(),
                command.priority(), command.assignedUserId(), command.dueDate());

        return repository.save(task);
    }

    @Override
    public List<Task> list(String ownerEmail, TaskStatus status) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);
        return repository.findByHousehold(householdId, status);
    }

    @Override
    public Task changeStatus(Long taskId, String ownerEmail, TaskStatus newStatus) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);

        Task existing = repository.findByIdAndHouseholdId(taskId, householdId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        return repository.save(existing.changeStatus(newStatus));
    }

    @Override
    public void delete(Long taskId, String ownerEmail) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);

        Task existing = repository.findByIdAndHouseholdId(taskId, householdId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        repository.deleteById(existing.getId());
    }

    @Override
    public Map<Long, String> assigneeNames(String ownerEmail) {
        Long householdId = householdResolver.requireHouseholdId(ownerEmail);
        return assigneeLookup.memberNames(householdId, ownerEmail);
    }

    /** El responsable, si hay, debe ser miembro del hogar de la tarea. */
    private void requireMember(Long householdId, String requesterEmail, Long assignedUserId) {
        if (assignedUserId == null) {
            return;
        }
        if (!assigneeLookup.memberNames(householdId, requesterEmail).containsKey(assignedUserId)) {
            throw new DomainValidationException("error.task.assignee.notMember",
                    "El responsable debe ser miembro del hogar");
        }
    }
}
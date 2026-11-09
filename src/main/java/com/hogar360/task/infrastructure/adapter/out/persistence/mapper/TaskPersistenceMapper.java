package com.hogar360.task.infrastructure.adapter.out.persistence.mapper;

import com.hogar360.task.domain.model.Task;
import com.hogar360.task.infrastructure.adapter.out.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskPersistenceMapper {

    public TaskEntity toEntity(Task task) {
        return new TaskEntity(
                task.getId(),
                task.getHouseholdId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getAssignedUserId(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getCompletedAt()
        );
    }

    public Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getHouseholdId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getAssignedUserId(),
                entity.getDueDate(),
                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }
}
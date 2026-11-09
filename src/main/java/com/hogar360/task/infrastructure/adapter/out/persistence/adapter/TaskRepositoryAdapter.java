package com.hogar360.task.infrastructure.adapter.out.persistence.adapter;

import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskStatus;
import com.hogar360.task.domain.port.out.TaskRepositoryPort;
import com.hogar360.task.infrastructure.adapter.out.persistence.entity.TaskEntity;
import com.hogar360.task.infrastructure.adapter.out.persistence.mapper.TaskPersistenceMapper;
import com.hogar360.task.infrastructure.adapter.out.persistence.repository.TaskJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TaskRepositoryAdapter implements TaskRepositoryPort {

    private final TaskJpaRepository jpaRepository;
    private final TaskPersistenceMapper mapper;

    public TaskRepositoryAdapter(TaskJpaRepository jpaRepository, TaskPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Task save(Task task) {
        TaskEntity saved = jpaRepository.save(mapper.toEntity(task));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Task> findByIdAndHouseholdId(Long id, Long householdId) {
        return jpaRepository.findByIdAndHouseholdId(id, householdId).map(mapper::toDomain);
    }

    @Override
    public List<Task> findByHousehold(Long householdId, TaskStatus status) {
        List<TaskEntity> entities = (status == null)
                ? jpaRepository.findAllOfHousehold(householdId)
                : jpaRepository.findByStatusOfHousehold(householdId, status);

        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
package com.hogar360.task.infrastructure.adapter.out.statistics;

import com.hogar360.statistics.domain.model.TaskCounts;
import com.hogar360.statistics.domain.port.out.StatisticsTasksPort;
import com.hogar360.task.domain.model.Task;
import com.hogar360.task.domain.model.TaskStatus;
import com.hogar360.task.domain.port.out.TaskRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatisticsTasksAdapter implements StatisticsTasksPort {

    private final TaskRepositoryPort taskRepository;

    public StatisticsTasksAdapter(TaskRepositoryPort taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskCounts countTasks(Long householdId) {
        List<Task> tasks = taskRepository.findByHousehold(householdId, null);
        return new TaskCounts(
                count(tasks, TaskStatus.PENDING),
                count(tasks, TaskStatus.IN_PROGRESS),
                count(tasks, TaskStatus.COMPLETED));
    }

    private static int count(List<Task> tasks, TaskStatus status) {
        return (int) tasks.stream().filter(t -> t.getStatus() == status).count();
    }
}

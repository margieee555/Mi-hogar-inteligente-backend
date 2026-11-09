package com.hogar360.statistics.domain.port.out;

import com.hogar360.statistics.domain.model.TaskCounts;

public interface StatisticsTasksPort {
    TaskCounts countTasks(Long householdId);
}
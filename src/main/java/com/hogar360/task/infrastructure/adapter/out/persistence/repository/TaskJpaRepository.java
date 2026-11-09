package com.hogar360.task.infrastructure.adapter.out.persistence.repository;

import com.hogar360.task.domain.model.TaskStatus;
import com.hogar360.task.infrastructure.adapter.out.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByIdAndHouseholdId(Long id, Long householdId);

    @Query("""
            select t from TaskEntity t
            where t.householdId = :householdId
            order by case when t.dueDate is null then 1 else 0 end, t.dueDate asc, t.id desc
            """)
    List<TaskEntity> findAllOfHousehold(@Param("householdId") Long householdId);

    @Query("""
            select t from TaskEntity t
            where t.householdId = :householdId
              and t.status = :status
            order by case when t.dueDate is null then 1 else 0 end, t.dueDate asc, t.id desc
            """)
    List<TaskEntity> findByStatusOfHousehold(@Param("householdId") Long householdId,
                                             @Param("status") TaskStatus status);
}
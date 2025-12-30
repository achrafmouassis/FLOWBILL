package com.flowbill.project.repository;

import com.flowbill.project.entity.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, TaskDependency.TaskDependencyId> {

    @Query("SELECT td FROM TaskDependency td WHERE td.blocked.id = :taskId")
    List<TaskDependency> findBlockersForTask(@Param("taskId") Long taskId);

    @Query("SELECT td FROM TaskDependency td WHERE td.blocker.id = :taskId")
    List<TaskDependency> findTasksBlockedBy(@Param("taskId") Long taskId);
}

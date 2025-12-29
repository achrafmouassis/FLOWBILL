package com.flowbill.project.repository;

import com.flowbill.project.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    java.util.Optional<Task> findByIdAndTenantId(Long id, String tenantId);

    java.util.List<Task> findByProjectId(Long projectId);

    java.util.List<Task> findBySprintId(Long sprintId);

    java.util.List<Task> findByProjectIdAndSprintIsNull(Long projectId);

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(Long projectId, String status);
}

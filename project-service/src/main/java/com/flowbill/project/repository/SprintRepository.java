package com.flowbill.project.repository;

import com.flowbill.project.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    java.util.Optional<Sprint> findByIdAndTenantId(Long id, String tenantId);

    List<Sprint> findByProjectId(Long projectId);

    List<Sprint> findByStatus(Sprint.SprintStatus status);

    long countByProjectIdAndStatus(Long projectId, Sprint.SprintStatus status);
}

package com.flowbill.project.repository;

import com.flowbill.project.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    java.util.Optional<Sprint> findByIdAndTenantId(Long id, String tenantId);

    long countByTenantIdAndStatus(String tenantId, Sprint.SprintStatus status);

    List<Sprint> findByProjectIdAndTenantId(Long projectId, String tenantId);

    List<Sprint> findByStatusAndTenantId(Sprint.SprintStatus status, String tenantId);

    List<Sprint> findByStatus(Sprint.SprintStatus status);

    long countByProjectIdAndStatusAndTenantId(Long projectId, Sprint.SprintStatus status, String tenantId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT s FROM Sprint s " +
            "JOIN Task t ON t.sprint.id = s.id " +
            "WHERE t.assignedUserId = :userId " +
            "AND s.tenantId = :tenantId " +
            "AND s.status IN ('PLANNED', 'ACTIVE') " +
            "ORDER BY " +
            "    CASE s.status " +
            "        WHEN 'ACTIVE' THEN 1 " +
            "        WHEN 'PLANNED' THEN 2 " +
            "    ELSE 3 END, " +
            "    s.startDate DESC")
    List<Sprint> findSprintsForDeveloper(@org.springframework.data.repository.query.Param("userId") Long userId,
            @org.springframework.data.repository.query.Param("tenantId") String tenantId);
}

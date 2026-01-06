package com.flowbill.project.repository;

import com.flowbill.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

        java.util.Optional<Project> findByIdAndTenantId(Long id, String tenantId);

        List<Project> findAllByTenantId(String tenantId);

        long countByTenantIdAndStatus(String tenantId, Project.ProjectStatus status);

        @Query("SELECT p FROM Project p WHERE p.tenantId = :tenantId " +
                        "AND (:status IS NULL OR p.status = :status) " +
                        "AND (:type IS NULL OR p.type = :type) " +
                        "AND (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(p.code) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(p.clientName) LIKE LOWER(CONCAT('%', :query, '%')))")
        List<Project> searchProjects(
                        @Param("tenantId") String tenantId,
                        @Param("status") Project.ProjectStatus status,
                        @Param("type") String type,
                        @Param("query") String query);

        @Query("SELECT p FROM Project p JOIN p.team m " +
                        "WHERE p.tenantId = :tenantId AND m.userId = :userId")
        List<Project> findByTenantIdAndMemberUserId(
                        @Param("tenantId") String tenantId,
                        @Param("userId") Long userId);
}

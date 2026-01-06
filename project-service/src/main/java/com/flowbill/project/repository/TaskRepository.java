package com.flowbill.project.repository;

import com.flowbill.project.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
        java.util.Optional<Task> findByIdAndTenantId(Long id, String tenantId);

        long countByTenantId(String tenantId);

        long countByStatusAndTenantId(com.flowbill.project.enums.TaskStatus status, String tenantId);

        long countByStatusAndTenantIdAndUpdatedAtAfter(com.flowbill.project.enums.TaskStatus status, String tenantId,
                        java.time.LocalDateTime after);

        long countByTypeAndStatusAndTenantId(com.flowbill.project.enums.TaskType type,
                        com.flowbill.project.enums.TaskStatus status, String tenantId);

        @Query("SELECT COALESCE(SUM(t.estimation), 0) FROM Task t WHERE t.tenantId = :tenantId AND (t.sprint.status = 'ACTIVE' OR t.sprint IS NULL)")
        Long sumActiveEstimationByTenantId(@Param("tenantId") String tenantId);

        java.util.List<Task> findByProjectIdAndTenantId(Long projectId, String tenantId);

        java.util.List<Task> findBySprintIdAndTenantId(Long sprintId, String tenantId);

        java.util.List<Task> findByProjectIdAndSprintIsNullAndTenantId(Long projectId, String tenantId);

        long countByProjectIdAndTenantId(Long projectId, String tenantId);

        long countByProjectIdAndStatusAndTenantId(Long projectId, com.flowbill.project.enums.TaskStatus status,
                        String tenantId);

        // Agile / Backlog Queries

        @Query(value = "SELECT t FROM Task t " +
                        "WHERE t.project.id = :projectId " +
                        "AND t.type = 'STORY' " +
                        "AND (:search IS NULL OR LOWER(t.title) LIKE :search OR LOWER(t.description) LIKE :search) "
                        +
                        "AND (:moscow IS NULL OR t.moscowPriority = :moscow) " +
                        "AND (:sprintId IS NULL OR t.sprint.id = :sprintId) " +
                        "AND (:unplanned = true AND t.sprint IS NULL OR :unplanned = false) " +
                        "AND t.tenantId = :tenantId " +
                        "ORDER BY t.wsjfScore DESC NULLS LAST, t.manualOrder ASC", countQuery = "SELECT COUNT(t) FROM Task t "
                                        +
                                        "WHERE t.project.id = :projectId " +
                                        "AND t.type = 'STORY' " +
                                        "AND (:search IS NULL OR LOWER(t.title) LIKE :search OR LOWER(t.description) LIKE :search) "
                                        +
                                        "AND (:moscow IS NULL OR t.moscowPriority = :moscow) " +
                                        "AND (:sprintId IS NULL OR t.sprint.id = :sprintId) " +
                                        "AND (:unplanned = true AND t.sprint IS NULL OR :unplanned = false) " +
                                        "AND t.tenantId = :tenantId")
        org.springframework.data.domain.Page<Task> searchBacklogStories(@Param("projectId") Long projectId,
                        @Param("search") String search,
                        @Param("moscow") String moscow,
                        @Param("sprintId") Long sprintId,
                        @Param("unplanned") Boolean unplanned,
                        @Param("tenantId") String tenantId,
                        org.springframework.data.domain.Pageable pageable);

        @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.type = 'STORY' AND t.tenantId = :tenantId")
        java.util.List<Task> findAllStoriesByProject(@Param("projectId") Long projectId,
                        @Param("tenantId") String tenantId);

        // --- New Agile Queries ---

        @Query("SELECT DISTINCT t FROM Task t " +
                        "WHERE t.project.id = :projectId " +
                        "AND t.type = 'STORY' " +
                        "AND (:moscow IS NULL OR t.moscowPriority IN :moscow) " +
                        "AND (:sprintId IS NULL OR t.sprint.id = :sprintId OR (:sprintId = -1 AND t.sprint IS NULL)) "
                        +
                        "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) "
                        +
                        "AND t.tenantId = :tenantId " +
                        "ORDER BY CASE WHEN :sort = 'wsjf' THEN t.wsjfScore ELSE 0 END DESC, " +
                        "CASE WHEN :sort = 'moscow' THEN " +
                        "  CASE t.moscowPriority WHEN 'MUST_HAVE' THEN 1 WHEN 'SHOULD_HAVE' THEN 2 WHEN 'COULD_HAVE' THEN 3 WHEN 'WONT_HAVE' THEN 4 ELSE 5 END "
                        +
                        "ELSE 0 END ASC, " +
                        "CASE WHEN :sort = 'estimation' THEN t.estimation ELSE 0 END DESC, " +
                        "t.createdAt DESC")
        java.util.List<Task> searchBacklogStories(
                        @Param("projectId") Long projectId,
                        @Param("moscow") java.util.List<String> moscow,
                        @Param("sprintId") Long sprintId,
                        @Param("search") String search,
                        @Param("sort") String sort,
                        @Param("tenantId") String tenantId);

        @Query("SELECT DISTINCT t FROM Task t " +
                        "WHERE t.project.id = :projectId " +
                        "AND t.type = 'STORY' " +
                        "AND EXISTS (SELECT 1 FROM Task child WHERE child.parentStory = t AND child.assignedUserId = :userId) "
                        +
                        "AND (:moscow IS NULL OR t.moscowPriority IN :moscow) " +
                        "AND (:sprintId IS NULL OR t.sprint.id = :sprintId OR (:sprintId = -1 AND t.sprint IS NULL)) "
                        +
                        "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))) "
                        +
                        "AND t.tenantId = :tenantId " +
                        "ORDER BY CASE WHEN :sort = 'wsjf' THEN t.wsjfScore ELSE 0 END DESC, " +
                        "CASE WHEN :sort = 'moscow' THEN " +
                        "  CASE t.moscowPriority WHEN 'MUST_HAVE' THEN 1 WHEN 'SHOULD_HAVE' THEN 2 WHEN 'COULD_HAVE' THEN 3 WHEN 'WONT_HAVE' THEN 4 ELSE 5 END "
                        +
                        "ELSE 0 END ASC, " +
                        "t.createdAt DESC")
        java.util.List<Task> searchBacklogStoriesForDeveloper(
                        @Param("projectId") Long projectId,
                        @Param("userId") Long userId,
                        @Param("moscow") java.util.List<String> moscow,
                        @Param("sprintId") Long sprintId,
                        @Param("search") String search,
                        @Param("sort") String sort,
                        @Param("tenantId") String tenantId);

        @Query("SELECT DISTINCT t FROM Task t " +
                        "WHERE t.type = 'STORY' " +
                        "AND t.sprint IS NULL " +
                        "AND t.tenantId = :tenantId " +
                        "AND EXISTS (SELECT 1 FROM Task child WHERE child.parentStory = t AND child.assignedUserId = :userId)")
        java.util.List<Task> findStoriesWithAssignedTasksForDeveloper(@Param("userId") Long userId,
                        @Param("tenantId") String tenantId);

        @Query("SELECT t FROM Task t WHERE t.id IN (SELECT td.blocker.id FROM TaskDependency td WHERE td.blocked.id = :taskId) AND t.tenantId = :tenantId")
        java.util.List<Task> findBlockersForTask(@Param("taskId") Long taskId, @Param("tenantId") String tenantId);

        @Query("SELECT COALESCE(SUM(t.estimation), 0) FROM Task t WHERE t.sprint.id = :sprintId AND t.type = 'STORY' AND t.tenantId = :tenantId")
        Integer sumEstimationBySprintId(@Param("sprintId") Long sprintId, @Param("tenantId") String tenantId);

        @Query("SELECT COALESCE(SUM(t.estimation), 0) FROM Task t WHERE t.sprint.id = :sprintId AND t.status = 'DONE' AND t.type = 'STORY' AND t.tenantId = :tenantId")
        Integer sumCompletedStoryPointsBySprintId(@Param("sprintId") Long sprintId, @Param("tenantId") String tenantId);

        boolean existsByProject_IdAndAssignedUserId(Long projectId, Long userId);

        // Stats Queries
        long countByProjectIdAndTypeAndTenantId(Long projectId, com.flowbill.project.enums.TaskType type,
                        String tenantId);

        long countByProjectIdAndTypeAndMoscowPriorityAndTenantId(Long projectId,
                        com.flowbill.project.enums.TaskType type,
                        com.flowbill.project.enums.MoscowPriority moscowPriority, String tenantId);

        @Query("SELECT CAST(COALESCE(SUM(t.estimation), 0) AS long) FROM Task t WHERE t.project.id = :projectId AND t.type = 'STORY' AND t.tenantId = :tenantId")
        Long sumEstimationByProjectId(@Param("projectId") Long projectId, @Param("tenantId") String tenantId);

        @Query("SELECT COALESCE(AVG(t.wsjfScore), 0.0) FROM Task t WHERE t.project.id = :projectId AND t.type = 'STORY' AND t.tenantId = :tenantId")
        Double avgWsjfScoreByProjectId(@Param("projectId") Long projectId, @Param("tenantId") String tenantId);

        @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.type = 'STORY' AND t.sprint IS NULL AND t.tenantId = :tenantId")
        Long countUnplannedStoriesByProjectId(@Param("projectId") Long projectId, @Param("tenantId") String tenantId);

        @Query("SELECT DISTINCT t FROM Task t " +
                        "WHERE t.assignedUserId = :userId " +
                        "AND t.tenantId = :tenantId " +
                        "AND (:statusFilters IS NULL OR t.status IN :statusFilters) " +
                        "AND (:sprintId IS NULL OR " +
                        "     (:sprintId = -1L AND t.sprint IS NULL) OR " +
                        "     t.sprint.id = :sprintId) " +
                        "ORDER BY " +
                        "    CASE WHEN :sortBy = 'priority' THEN " +
                        "        CASE t.priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 ELSE 4 END "
                        +
                        "    ELSE 0 END ASC, " +
                        "    CASE WHEN :sortBy = 'estimation' THEN t.estimation ELSE 0 END DESC, " +
                        "    CASE WHEN :sortBy = 'created_date' THEN t.createdAt ELSE CURRENT_TIMESTAMP END DESC")
        java.util.List<Task> findTasksForDeveloper(
                        @Param("userId") Long userId,
                        @Param("statusFilters") java.util.List<String> statusFilters,
                        @Param("sprintId") Long sprintId,
                        @Param("sortBy") String sortBy,
                        @Param("tenantId") String tenantId);

        java.util.List<Task> findBySprintIdAndAssignedUserId(Long sprintId, Long assignedUserId);
}
